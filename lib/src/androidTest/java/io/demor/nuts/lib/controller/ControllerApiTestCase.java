package io.demor.nuts.lib.controller;

import android.test.AndroidTestCase;
import com.google.common.reflect.Reflection;
import com.google.gson.Gson;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import io.demor.nuts.lib.api.BaseResponse;
import io.demor.nuts.lib.net.ApiInvokeHandler;
import io.demor.nuts.lib.net.ApiRequest;
import io.demor.nuts.lib.net.JsonNet;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ControllerApiTestCase extends AndroidTestCase {

    MockWebServer mServer;

    TestApi2 mApi;
    
    TestController mController = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(TestController.IMPL));

    @Override
    public void setUp() throws Exception {
        mServer = new MockWebServer();
        mServer.start();
        mApi = Reflection.newProxy(TestApi2.class, new ApiInvokeHandler(new JsonNet(new Gson()) {
            @Override
            protected void handleRequest(ApiRequest request, Method method, Object[] args) {
                request.setUrl(mServer.getUrl(request.getUrl()).toString());
            }
        }));
    }

    @Override
    public void tearDown() throws Exception {
        mServer.shutdown();
    }

    public void testSingle() throws Exception {
        BaseResponse firstResponse = new BaseResponse();
        firstResponse.code = 101;
        mServer.enqueue(new MockResponse().setBody(new Gson().toJson(firstResponse)));

        BaseResponse resultResponse = mController.single(mApi, 100)
                .sync();

        assertEquals(101, resultResponse.code);
    }

    public void testMultiTask() throws Exception {
        final int count = 1000;

        final CountDownLatch latch = new CountDownLatch(count);

        for (int i = 0; i < count; ++i) {
            BaseResponse firstResponse = new BaseResponse();
            firstResponse.code = i + 1;
            mServer.enqueue(new MockResponse().setBody(new Gson().toJson(firstResponse))
                    .setBodyDelay(200, TimeUnit.MILLISECONDS));

            switch (i % 3) {
                case 0:
                    mController.single(mApi, 100)
                            .asyncUI(new ControllerCallback<BaseResponse>() {
                                @Override
                                public void onResult(final BaseResponse baseResponse) {
                                    assertNotNull(baseResponse);
                                    assertNotSame(0, baseResponse.code);
                                    assertEquals(BaseResponse.SUCCESS, baseResponse.getErrorCode());
                                    latch.countDown();
                                }
                            });
                    break;
                case 1:
                    mController.single(mApi, 100)
                            .addListener(new ControllerListener() {
                                @Override
                                public void onBegin() {

                                }

                                @Override
                                public void onEnd(final Object obj) {
                                    BaseResponse response = (BaseResponse) obj;
                                    assertNotNull(response);
                                    assertNotSame(0, response.code);
                                    assertEquals(BaseResponse.SUCCESS, response.getErrorCode());
                                    latch.countDown();
                                }

                                @Override
                                public void onException(final Throwable throwable) {

                                }
                            });
                    break;
                case 2:
                    BaseResponse response = mController.single(mApi, 100)
                            .sync();
                    assertNotNull(response);
                    assertNotSame(0, response.code);
                    assertEquals(BaseResponse.SUCCESS, response.getErrorCode());
                    latch.countDown();
                    break;
            }

        }

        latch.await();
        assertEquals(0, latch.getCount());
    }

    public void testMultiAsync() throws Exception {
        final int count = 1000;

        final CountDownLatch latch = new CountDownLatch(count);

        for (int i = 0; i < count; ++i) {
            BaseResponse firstResponse = new BaseResponse();
            firstResponse.code = i + 1;
            mServer.enqueue(new MockResponse().setBody(new Gson().toJson(firstResponse)));
        }

        for (int i = 0; i < count; ++i) {
            mController.single(mApi, 100)
                    .asyncUI(new ControllerCallback<BaseResponse>() {
                        @Override
                        public void onResult(final BaseResponse baseResponse) {
                            latch.countDown();
                        }
                    });
        }

        latch.await();
        assertEquals(0, latch.getCount());
    }
}
