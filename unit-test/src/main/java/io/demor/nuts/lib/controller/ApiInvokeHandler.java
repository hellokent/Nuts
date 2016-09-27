package io.demor.nuts.lib.controller;

import com.google.common.base.Strings;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import io.demor.nuts.lib.ReflectUtils;
import io.demor.nuts.lib.client.TestClient;
import io.demor.nuts.lib.model.GsonObject;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ApiInvokeHandler implements InvocationHandler {

    final OkHttpClient mHttpClient;
    final int mPort;

    public ApiInvokeHandler(int port) {
        mHttpClient = new OkHttpClient();
        mPort = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String className = proxy.getClass().getName();
        if (Strings.isNullOrEmpty(className)) {
            return null;
        }
        final Class<?> actualReturnClz;
        if (ReflectUtils.isSubclassOf(method.getReturnType(), Return.class)) {
            actualReturnClz = (Class<?>) ReflectUtils.getGenericType(method.getReturnType());
        } else {
            actualReturnClz = method.getReturnType();
        }
        return TestClient.GSON.fromJson(callController(className, method, args), actualReturnClz);
    }


    private String callController(final String className, final Method method, final Object[] args) throws IOException {

        final GsonObject[] gsonArgs = new GsonObject[args.length];
        for (int i = 0; i < args.length; ++i) {
            gsonArgs[i] = new GsonObject(args[i]);
        }

        return mHttpClient.newCall(new Request.Builder()
                .url(String.format("http://localhost:%s/c/%s/%s", mPort, className, method.getName()))
                .post(RequestBody.create(MediaType.parse("application/json"), TestClient.GSON.toJson(gsonArgs)))
                .build())
                .execute().body().string();
    }
}
