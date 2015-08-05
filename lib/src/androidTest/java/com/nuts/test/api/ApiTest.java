package com.nuts.test.api;

import android.test.AndroidTestCase;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Splitter;
import com.google.common.reflect.Reflection;
import com.google.gson.Gson;
import com.nuts.lib.net.ApiInvokeHandler;
import com.nuts.lib.net.INet;
import com.nuts.lib.net.IResponse;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

public class ApiTest extends AndroidTestCase {

    final Gson mGson = new Gson();

    final MockWebServer mServer = new MockWebServer();

    TestApi mApi;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mServer.start();

        mApi = Reflection.newProxy(TestApi.class, new ApiInvokeHandler(new INet() {
            @Override
            protected String onCreateUrl(final String url, final Method method, final Object[] args) {
                return mServer.getUrl("/" + url)
                        .toString();
            }

            @Override
            protected void onCreateParams(final TreeMap<String, String> params, final TreeMap<String, String>
                    headers, final Method method, final Object[] args) {

            }
        }, mGson));
    }

    @Override
    public void tearDown() throws Exception {
        mServer.shutdown();
    }

    public void testGet() throws Exception {
        BaseResponse response = new BaseResponse();
        response.msg = "asdf";
        mServer.enqueue(new MockResponse().setBody(mGson.toJson(response)));
        assertEquals("asdf", mApi.test("a1", "b2").msg);
        final RecordedRequest request = mServer.takeRequest();
        assertTrue(request.getPath()
                .startsWith("/test"));
        final Map<String, String> queryMap = Splitter.on('&')
                .withKeyValueSeparator("=")
                .split(request.getPath()
                        .substring("/test".length() + 1));

        assertTrue(queryMap.containsKey("a"));
        assertTrue(queryMap.containsKey("b"));
        assertEquals("a1", queryMap.get("a"));
        assertEquals("b2", queryMap.get("b"));
        assertEquals(request.getMethod()
                .toLowerCase(), "get");
    }

    public void test502() throws Exception {
        mServer.enqueue(new MockResponse().setResponseCode(502)
                .setBody(mGson.toJson(new BaseResponse())));

        BaseResponse response = mApi.test("a", "b");
        assertEquals(502, response.getStatusCode());
    }

    public void testInvalidJson() throws Exception {
        mServer.enqueue(new MockResponse().setBody(mGson.toJson("<>")));
        BaseResponse response = mApi.test("", "");
        assertEquals(IResponse.BAD_NETWORK, response.getErrorCode());
    }
}
