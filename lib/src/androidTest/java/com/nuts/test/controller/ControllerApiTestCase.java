package com.nuts.test.controller;

import android.test.AndroidTestCase;

import java.lang.reflect.Method;
import java.util.TreeMap;

import com.google.common.reflect.Reflection;
import com.google.gson.Gson;
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
}
