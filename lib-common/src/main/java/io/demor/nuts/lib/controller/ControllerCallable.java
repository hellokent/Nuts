package io.demor.nuts.lib.controller;

import com.google.common.base.MoreObjects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

class ControllerCallable implements Callable<Object> {

    final Method mMethod;
    final Object mImpl;
    final Object[] mArgs;

    public ControllerCallable(final Method method, final Object impl, final Object[] args) {
        mMethod = method;
        mImpl = impl;
        mArgs = args;
        mMethod.setAccessible(true);
    }

    @Override
    public Object call() throws InvocationTargetException, IllegalAccessException {
        return mMethod.invoke(mImpl, mArgs);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("method", mMethod)
                .add("impl", mImpl.getClass().getName())
                .toString();
    }
}
