package io.demor.nuts.lib.controller;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public final class ReturnImpl<T> extends Return<T> {

    public ReturnImpl(T data) {
        super(data);
    }

    public ReturnImpl(Callable<Object> callable, Method method) {
        super(callable, method);
    }

    @Override
    public T sync() {
        return null;
    }

    @Override
    public void asyncUI(ControllerCallback<T> callback) {

    }

    @Override
    public Return<T> setNeedCheckActivity(boolean needCheckActivity) {
        return null;
    }

    @Override
    public Return<T> addListener(ControllerListener listener) {
        return null;
    }

    @Override
    public Return<T> setTimeout(int time, TimeUnit unit, TimeoutListener listener) {
        return null;
    }
}
