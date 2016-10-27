package io.demor.nuts.lib.controller;

import org.joor.Reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ControllerInvokeHandler<I> implements InvocationHandler {

    protected AppInstance mApp;

    public ControllerInvokeHandler(AppInstance app) {
        mApp = app;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return Reflect.on(ReturnImpl.class)
                .create(method, mApp.mHost, mApp.mPort, args)
                .get();
    }

}
