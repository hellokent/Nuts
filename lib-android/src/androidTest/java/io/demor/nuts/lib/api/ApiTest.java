package io.demor.nuts.lib.api;

import android.test.AndroidTestCase;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.reflect.Reflection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import io.demor.nuts.lib.net.*;

import java.lang.reflect.Method;
import java.util.Map;

public class ApiTest extends AndroidTestCase {

    private final Gson mGson = new GsonBuilder().addSerializationExclusionStrategy(new GsonSerializeExclusionStrategy())
            .addDeserializationExclusionStrategy(new GsonDeserializeExclusionStrategy())
            .create();

    private final MockWebServer mServer = new MockWebServer();

    private TestApi mApi;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mServer.start();

        mApi = Reflection.newProxy(TestApi.class, new ApiInvokeHandler(new JsonNet(mGson) {

            @Override
            protected void handleRequest(ApiRequest request, Method method, Object[] args) {
                switch (request.getUrl()) {
                    case "url":
                        request.setUrl("http://localhost:1234/invalidUrl");
                    case "empty":
                        request.setUrl("");
                    default:
                        request.setUrl(mServer.getUrl("/" + request.getUrl()).toString());
                }
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
        assertEquals(BaseResponse.ILLEGAL_JSON, response.getErrorCode());
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

        mServer.enqueue(new MockResponse().setBody(mGson.toJson(new BaseResponse())));
        mApi.header1("header");
        request = mServer.takeRequest();
        assertEquals("header", request.getHeader("p1"));
    }

    public void testGson() {
        BaseResponse response = new BaseResponse();
        String json = mGson.toJson(response)
                .toLowerCase();
        assertFalse(json.contains("header"));
    }

    public void testParamUrl() throws Exception {
        mServer.enqueue(new MockResponse().setBody(mGson.toJson(new BaseResponse())));

        mApi.testParamUrl("123");

        RecordedRequest request = mServer.takeRequest();
        assertEquals("/123/user", request.getPath());
    }

    public void testParamList() throws Exception {
        mServer.enqueue(new MockResponse().setBody(mGson.toJson(new BaseResponse())));

        final ParamList list1 = new ParamList.Builder().skipNullValue(false).nullValue("null").build();
        list1.add("test", null);
        list1.add("int", 1);
        assertEquals("null", list1.getData().get("test"));

        final ParamList list2 = new ParamList.Builder().skipNullValue(true).build();
        list2.add("test1", null);
        list2.add("long", 2L);
        assertFalse(list2.getData().containsKey("test1"));

        mApi.testParamList(list1, list2);

        RecordedRequest request = mServer.takeRequest();
        final Map<String, String> queryMap = Splitter.on('&')
                .withKeyValueSeparator("=")
                .split(request.getPath()
                        .substring("/paramList".length() + 1));

        assertEquals("null", queryMap.get("test"));
        assertEquals("1", queryMap.get("int"));
        assertEquals("2", queryMap.get("long"));
    }

    public void testParamMap() throws Exception {
        Map<Integer, Long> map = Maps.newHashMap();
        map.put(1, 2L);
        map.put(3, null);
        map.put(null, 4L);

        mServer.enqueue(new MockResponse().setBody(mGson.toJson(new BaseResponse())));

        mApi.testMap(map);
        RecordedRequest request = mServer.takeRequest();
        final Map<String, String> queryMap = Splitter.on('&')
                .withKeyValueSeparator("=")
                .split(request.getPath()
                        .substring("/map".length() + 1));

        assertEquals("2", queryMap.get("1"));
        assertFalse(queryMap.containsKey("3"));
    }

    public void testMap2() throws Exception {
        Map<String, Integer> map = Maps.newHashMap();
        map.put("key1", 1);

        ParamList list = new ParamList.Builder().skipNullValue(true).build();
        list.add("key2", "2");

        mServer.enqueue(new MockResponse().setBody(mGson.toJson(new BaseResponse())));
        mApi.testMap2(map, list, "3");

        RecordedRequest request = mServer.takeRequest();
        final Map<String, String> queryMap = Splitter.on('&')
                .withKeyValueSeparator("=")
                .split(request.getPath()
                        .substring("/map2".length() + 1));

        assertEquals("1", queryMap.get("key1"));
        assertEquals("2", queryMap.get("key2"));
        assertEquals("3", queryMap.get("param"));
    }

    public void testRetry() throws Exception {
        mServer.enqueue(new MockResponse().setStatus("404"));
        mServer.enqueue(new MockResponse().setStatus("502"));
        mServer.enqueue(new MockResponse().setBody(mGson.toJson(new BaseResponse())));

        BaseResponse response = mApi.retry();
        assertEquals("hello", response.msg);
    }
}
