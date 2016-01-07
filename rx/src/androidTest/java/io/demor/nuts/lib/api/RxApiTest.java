package io.demor.nuts.lib.api;

import android.os.Looper;
import android.test.AndroidTestCase;
import com.google.common.collect.Maps;
import com.google.common.reflect.Reflection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import io.demor.nuts.lib.net.*;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

public class RxApiTest extends AndroidTestCase {
    private final Gson mGson = new GsonBuilder().addSerializationExclusionStrategy(new GsonSerializeExclusionStrategy())
            .addDeserializationExclusionStrategy(new GsonDeserializeExclusionStrategy())
            .create();

    private final MockWebServer mServer = new MockWebServer();

    private TestApi mApi;

    @Override
    public void setUp() throws Exception {
        mServer.start();

        mApi = Reflection.newProxy(TestApi.class, new RxApiHandler(new JsonNet(mGson) {

            @Override
            protected void handleRequest(ApiRequest request, Method method, Object[] args) {
                request.setUrl(mServer.getUrl("/" + request.getUrl()).toString());
            }

            @Override
            public Object createResponse(Class clz, ApiResponse response) {
                BaseResponse r;
                if (response.isSuccess()) {
                    try {
                        r = (BaseResponse) mGson.fromJson(new String(response.getResult()), clz);
                        r.setErrorCode(BaseResponse.SUCCESS);
                    } catch (Throwable e) {
                        try {
                            r = (BaseResponse) clz.newInstance();
                            r.setErrorCode(BaseResponse.ILLEGAL_JSON);
                        } catch (Exception e1) {
                            throw new Error(e1);
                        }
                    }
                } else {
                    try {
                        r = (BaseResponse) clz.newInstance();
                        r.setErrorCode(BaseResponse.BAD_NETWORK);
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
                r.setStatusCode(response.getStatusCode());
                r.setHeader(Maps.newHashMap(response.getHeader()));
                return r;
            }
        }).setApiCallback(new ApiCallback() {
            @Override
            public ApiResponse handle(ApiRequest request) {
                System.out.printf("url:%s, param:%s, header:%s\n", request.getUrl(),
                        ApiRequest.joinMap(",", "=", request.getParams()),
                        ApiRequest.joinMap(",", "=", request.getHeaders()));
                final ApiResponse result = request.execute();
                if (result.isSuccess()) {
                    System.out.printf("OK: code:%s, msg:%s\n", result.getStatusCode(), new String(result.getResult()));
                } else {
                    System.out.printf("FAILED: e:%s\n", result.getException().getMessage());
                }
                return result;
            }
        }));
    }

    @Override
    public void tearDown() throws Exception {
        mServer.shutdown();
    }

    public void testGet() throws Exception {
        BaseResponse response = new BaseResponse();
        response.msg = "asdf";
        mServer.enqueue(new MockResponse().setBody(mGson.toJson(response)));
        final CountDownLatch latch = new CountDownLatch(2);
        mApi.test("a", "b")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse>() {
                    @Override
                    public void onCompleted() {
                        latch.countDown();
                        assertEquals(0, latch.getCount());
                        assertEquals(Thread.currentThread(), Looper.getMainLooper().getThread());
                    }

                    @Override
                    public void onError(Throwable e) {
                        assertEquals(Thread.currentThread(), Looper.getMainLooper().getThread());
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        latch.countDown();
                        assertEquals("asdf", baseResponse.msg);
                        assertEquals(Thread.currentThread(), Looper.getMainLooper().getThread());
                        try {
                            final RecordedRequest request = mServer.takeRequest();
                            assertTrue(request.getPath().startsWith("/test"));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
        latch.await();
    }
}
