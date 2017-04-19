package io.demor.nuts.lib.controller;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import io.demor.nuts.lib.ReflectUtils;
import io.demor.nuts.lib.module.ControllerInvocationResponse;
import org.joor.Reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static io.demor.nuts.lib.controller.MethodInfoUtil.GSON;

public class ControllerInvokeHandler implements InvocationHandler {

    protected AppInstance mApp;

    public ControllerInvokeHandler(AppInstance app) {
        mApp = app;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (ReflectUtils.isSubclassOf(method.getReturnType(), Return.class)) {
            return Reflect.on(ReturnImpl.class)
                    .create(method, mApp.mHost, mApp.mHttpPort, args)
                    .get();
        } else {
            String resp = new OkHttpClient().newCall(new Request.Builder()
                    .url(mApp.getApiUrl() + "controller/" + method.getDeclaringClass().getName())
                    .header("content-type", "application/json")
                    .post(RequestBody.create(MediaType.parse("application/json"), MethodInfoUtil.generateMethodInfo(method, args)))
                    .build())
                    .execute()
                    .body()
                    .string();
            ControllerInvocationResponse response = GSON.fromJson(resp, ControllerInvocationResponse.class);
            return GSON.fromJson(response.mData, method.getReturnType());
        }
    }

}
