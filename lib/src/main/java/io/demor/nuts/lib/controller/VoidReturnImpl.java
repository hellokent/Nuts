package io.demor.nuts.lib.controller;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class VoidReturnImpl extends VoidReturn {

    final ReturnImpl<Void> mImpl;

    public VoidReturnImpl() {
        super(null);
        mImpl = new ReturnImpl<>(null);
    }

    public VoidReturnImpl(Callable<Object> callable, Method method) {
        super(callable, method);
        mImpl = new ReturnImpl<>(callable, method);
    }

    @Override
    public Void sync() {
        mImpl.sync();
        return null;
    }

    @Override
    public void asyncUI(ControllerCallback<Void> callback) {
        mImpl.asyncUI(callback);
    }

    @Override
    public Return<Void> setNeedCheckActivity(boolean needCheckActivity) {
        return mImpl.setNeedCheckActivity(needCheckActivity);
    }

    @Override
    public Return<Void> addListener(ControllerListener listener) {
        return mImpl.addListener(listener);
    }

    @Override
    public Return<Void> setTimeout(int time, TimeUnit unit, TimeoutListener listener) {
        return mImpl.setTimeout(time, unit, listener);
    }
}
