package io.demor.nuts.common;

import android.app.Application;
import android.test.AndroidTestCase;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import io.demor.nuts.common.server.BaseWebServer;
import io.demor.nuts.common.server.IApi;

import java.io.IOException;
import java.util.Map;

public class BaseWebServerTest extends AndroidTestCase {

    BaseWebServer mWebServer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mWebServer = new BaseWebServer((Application) getContext().getApplicationContext(), new Gson(), 0);
        mWebServer.start();
    }

    public void testApi() throws Exception {
        final IApi api = new IApi() {
            @Override
            public String name() {
                return "test";
            }

            @Override
            public Object call(Map<String, String> param) {
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
            String resp = new OkHttpClient().newCall(new Request.Builder()
                    .url("http://localhost:" + mWebServer.getListeningPort() + "/api/test?arg1=value1")
                    .build()).execute().body().string();
            assertEquals("arg1-resp", resp);
            resp = new OkHttpClient().newCall(new Request.Builder()
                    .url("http://localhost:" + mWebServer.getListeningPort() + "/api/test")
                    .build()).execute().body().string();
            assertEquals("empty", resp);
        } catch (IOException e) {
            e.printStackTrace();
            assertNotNull(e);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mWebServer.stop();
    }
}
