package io.demor.nuts.lib.api;

import android.test.AndroidTestCase;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.reflect.Reflection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import io.demor.nuts.lib.net.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class ApiTest extends AndroidTestCase {

    final Gson mGson = new GsonBuilder().addSerializationExclusionStrategy(new GsonSerializeExclusionStrategy())
            .addDeserializationExclusionStrategy(new GsonDeserializeExclusionStrategy())
            .create();

    final MockWebServer mServer = new MockWebServer();

    TestApi mApi;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mServer.start();

        mApi = Reflection.newProxy(TestApi.class, new ApiInvokeHandler(new INet() {
            @Override
            protected String onCreateUrl(final String url, final Method method, final Object[] args) {
                switch (url) {
                    case "url":
                        return "http://localhost:1234/invalidUrl";
                    case "empty":
                        return "";
                    default:
                        return mServer.getUrl("/" + url)
                                .toString();
                }
            }

            @Override
            protected void onCreateParams(final TreeMap<String, String> params, final TreeMap<String, String>
                    headers, final Method method, final Object[] args) {

            }
        }, mGson).setApiCallback(new ApiCallback() {
            @Override
            public NetResult handle(final ApiProcess process, final String url, final Map<String, ?> param, final
            Map<String, String> header, final String method) {
                System.out.printf("url:%s, param:%s, header:%s\n", url, Joiner.on(",")
                                .withKeyValueSeparator("=")
                                .join(param), Joiner.on(",")
                                .withKeyValueSeparator("=")
                                .join(header));
                final NetResult result = process.execute();
                if (result.mIsSuccess) {
                    System.out.printf("OK: code:%s, msg:%s\n", result.mStatusCode, result.mStrResult);
                } else {
                    System.out.printf("FAILED: e:%s\n", result.mException.getMessage());
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

    public void testUrl() throws Exception {
        {
            BaseResponse response = mApi.testUrl();
            assertEquals(IResponse.BAD_NETWORK, response.getErrorCode());
        }

        {
            BaseResponse response = mApi.emptyUrl();
            assertEquals(IResponse.BAD_NETWORK, response.getErrorCode());
        }
    }

    public void testHeader() throws Exception {
        BaseResponse response = new BaseResponse();
        response.msg = "msg";
        mServer.enqueue(new MockResponse().addHeader("h1", "v1")
                .addHeader("h2", "v2")
                .setBody(mGson.toJson(response)));

        response = mApi.header();

        RecordedRequest request = mServer.takeRequest();
        assertEquals("h1", request.getHeader("r1"));
        assertEquals("h2", request.getHeader("r2"));
        assertNotSame("h3", request.getHeader("r3"));

        assertEquals("v1", response.getHeader("h1"));
        assertEquals("v2", response.getHeader("h2"));
    }

    public void testGson() {
        BaseResponse response = new BaseResponse();
        String json = mGson.toJson(response)
                .toLowerCase();
        assertFalse(json.contains("header"));
    }

    public void testParamUrl() throws Exception {
        BaseResponse response = new BaseResponse();
        mServer.enqueue(new MockResponse().setBody(mGson.toJson(response)));

        mApi.testParamUrl("123");

        RecordedRequest request = mServer.takeRequest();
        assertEquals("/123/user", request.getPath());
    }
}
