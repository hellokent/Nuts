package io.demor.nuts.lib.controller;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import io.demor.nuts.lib.module.AppInstanceResponse;

import java.io.IOException;

public class AppInstance {
    public String mHost;
    public int mHttpPort;
    public int mSocketPort;

    public AppInstance(String app, String host) throws IOException {
        mHost = host;
        System.out.println(String.format("http://%s:8080/api/application?name=%s", host, app));
        String resp = new OkHttpClient().newCall(new Request.Builder()
                .url(String.format("http://%s:8080/api/application?name=%s", host, app))
                .header("content-type", "application/json")
                .get()
                .build())
                .execute()
                .body()
                .string();
        AppInstanceResponse response = ControllerUtil.GSON.fromJson(resp, AppInstanceResponse.class);
        mHttpPort = response.mInstance.mHttpPort;
        mSocketPort = response.mInstance.mSocketPort;
    }

    public AppInstance(final String host, final int httpPort, final int socketPort) {
        mHost = host;
        mHttpPort = httpPort;
        mSocketPort = socketPort;
    }

    public final String getApiUrl() {
        return "http://" + mHost + ":" + mHttpPort + "/api/";
    }

    public final String getWebSocketUrl() {
        return "ws://" + mHost + ":" + mSocketPort + "/";
    }
}
