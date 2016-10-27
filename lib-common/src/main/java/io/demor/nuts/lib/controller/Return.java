package io.demor.nuts.lib.controller;

import com.google.common.collect.Lists;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class Return<T> {

    final List<ControllerListener<T>> mListeners = Lists.newCopyOnWriteArrayList();
    protected T mData;
    protected Method mMethod;

    public Return(final T data) {
        mData = data;
    }

    public Return(final Callable<Object> callable, Method method) {
        mMethod = method;
    }

    public abstract T sync();

    public abstract void asyncUI(final ControllerCallback<T> callback);

    public Return<T> setNeedCheckActivity(final boolean needCheckActivity) {
        return this;
    }

    public Return<T> addListener(final ControllerListener<T> listener) {
        mListeners.add(listener);
        return this;
    }

    public Return<T> setTimeout(int time, TimeUnit unit, TimeoutListener listener) {
        return this;
    }


    protected void callOnException(Throwable t) {
        for (ControllerListener l : mListeners) {
            l.onException(t);
        }
    }

    protected void callOnEnd(T data) {
        for (ControllerListener<T> l : mListeners) {
            l.onEnd(data);
        }
    }

    protected void callOnBegin() {
        for (ControllerListener l : mListeners) {
            l.onBegin();
        }
    }
}
