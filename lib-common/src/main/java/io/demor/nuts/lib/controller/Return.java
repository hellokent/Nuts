package io.demor.nuts.lib.controller;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class Return<T> {
    public Return(final T data) {
    }

    public Return(final Callable<Object> callable, Method method) {
    }

    public abstract T sync();

    public abstract void asyncUI(final ControllerCallback<T> callback);

    public abstract Return<T> setNeedCheckActivity(final boolean needCheckActivity);

    public abstract Return<T> addListener(final ControllerListener listener);

    public abstract Return<T> setTimeout(int time, TimeUnit unit, TimeoutListener listener);
}
