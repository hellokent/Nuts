package io.demor.nuts.lib.controller;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.lang.reflect.Method;

public final class ReturnImpl<T> extends Return<T> {

    private final String mHost;
    private final int mPort;
    private final OkHttpClient mClient;
    private final Object[] mArgs;

    public ReturnImpl(Method method, String host, int port, Object[] args) {
        super(null, method);
        mHost = host;
        mPort = port;
        mClient = new OkHttpClient();
        mArgs = args;
    }

    @Override
    public T sync() {
        try {
            String resp = mClient.newCall(new Request.Builder()
                    .url(String.format("http://%s:%d/api/controller", mHost, mPort))
                    .post(RequestBody.create(MediaType.parse("json"), ControllerUtil.generateControllerMethod(mMethod, mArgs)))
                    .build())
                    .execute()
                    .body()
                    .string();
            return (T) ControllerUtil.fromJson(resp, mMethod.getReturnType());
        } catch (IOException e) {
            throw new Error("bad network", e);
        }
    }

    @Override
    public void asyncUI(ControllerCallback<T> callback) {
        throw new Error("No ui thread in PC mode");
    }
}
