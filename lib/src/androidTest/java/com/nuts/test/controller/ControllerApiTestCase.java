package com.nuts.test.controller;

import android.test.AndroidTestCase;

import java.lang.reflect.Method;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.common.reflect.Reflection;
import com.google.gson.Gson;
import com.nuts.lib.controller.ControllerCallback;
import com.nuts.lib.controller.ControllerListener;
import com.nuts.lib.controller.ProxyInvokeHandler;
import com.nuts.lib.net.ApiInvokeHandler;
import com.nuts.lib.net.INet;
import com.nuts.test.api.BaseResponse;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

public class ControllerApiTestCase extends AndroidTestCase {

    MockWebServer mServer;

    TestApi2 mApi;

    TestController mController = new ProxyInvokeHandler<>(TestController.IMPL).createProxy();

    @Override
    public void setUp() throws Exception {
        mServer = new MockWebServer();
        mServer.start();
        mApi = Reflection.newProxy(TestApi2.class, new ApiInvokeHandler(new INet() {
            @Override
            protected String onCreateUrl(final String url, final Method method, final Object[] args) {
                return mServer.getUrl(url)
                        .toString();
            }

            @Override
            protected void onCreateParams(final TreeMap<String, String> params, final TreeMap<String, String>
                    headers, final Method method, final Object[] args) {
            }
        }, new Gson()));
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
        final int count = 100;

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
                                    assertTrue(baseResponse.getErrorCode() == 0);
                                    latch.countDown();
                                }
                            });
                    break;
                case 1:
                    mController.single(mApi, 100)
                            .addListener(new ControllerListener<BaseResponse>() {
                                @Override
                                public void onBegin() {

                                }

                                @Override
                                public void onEnd(final BaseResponse response) {
                                    assertNotNull(response);
                                    assertNotSame(0, response.code);
                                    assertTrue(response.getErrorCode() == 0);
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
                    assertTrue(response.getErrorCode() == 0);
                    latch.countDown();
                    break;
            }

        }

        //latch.await(200, TimeUnit.SECONDS);
        latch.await();
        assertEquals(0, latch.getCount());
    }
}
