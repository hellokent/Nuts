package io.demor.nuts.lib.server;

import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.Map;

import io.demor.nuts.lib.annotation.server.Url;

public class BaseWebServerTest extends AndroidTestCase {

    BaseWebServer mWebServer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mWebServer = new BaseWebServer(new AndroidServerClient(getContext()), new Gson(), 0);
        mWebServer.start();
    }

    public void testApi() throws Exception {
        final IApi api = new IApi() {
            @Override
            public String name() {
                return "test";
            }

            @Override
            public Object call(Map<String, String> param, String body) {
                assertNotNull(param);
                if (param.containsKey("arg1")) {
                    assertEquals("value1", param.get("arg1"));
                    return "arg1-resp";
                }
                return "empty";
            }
        };
        mWebServer.registerApi(api);
        assertNotSame(0, mWebServer.getListeningPort());
        try {
            String resp = get("test?arg1=value1");
            assertEquals("arg1-resp", resp);
            resp = get("test");
            assertEquals("empty", resp);
        } catch (IOException e) {
            e.printStackTrace();
            assertNotNull(e);
        }
    }

    public void testApiClass() {
        @Url("test")
        class Api {
            @Url("empty-api")
            public String empty() {
                return "empty";
            }

            @Url("/reg/")
            public String reg() {
                return "reg";
            }
        }
        mWebServer.registerApi(new Api());
        assertNotSame(0, mWebServer.getListeningPort());
        try {
            String resp = get("test/empty-api");
            assertEquals("empty", resp);
            resp = get("test/reg");
            assertEquals("reg", resp);
        } catch (IOException e) {
            e.printStackTrace();
            assertNotNull(e);
        }
    }

    public String get(String url) throws IOException {
        return new OkHttpClient().newCall(new Request.Builder()
                .url("http://localhost:" + mWebServer.getListeningPort() + "/api/" + url)
                .build()).execute().body().string();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mWebServer.stop();
    }
}
