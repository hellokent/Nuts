package io.demor.nuts.lib.eventbus;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import io.demor.nuts.lib.storage.Storage;

abstract class MethodContext {

    final Method mMethod;
    private final ThreadType mThreadType;
    private final Executor mBgExecutor;
    private final Executor mUiExecutor;

    MethodContext(final Method method, ThreadType threadType, final Executor bgExecutor, final Executor uiExecutor) {
        mMethod = method;
        mMethod.setAccessible(true);
        mThreadType = threadType;
        mBgExecutor = bgExecutor;
        mUiExecutor = uiExecutor;
    }

    void call(final Object obj, final Object... args) {
        switch (mThreadType) {
            case SOURCE:
                $call(obj, args);
                break;
            case MAIN:
                mUiExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        $call(obj, args);
                    }
                });
                break;
            case BACKGROUND:
                mBgExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        $call(obj, args);
                    }
                });
                break;
        }
    }

    private void $call(final Object obj, final Object... args) {
        if (obj == null) {
            return;
        }
        try {
            if (needDeepClone()) {
                mMethod.invoke(obj, Storage.CLONER.deepClone(args));
            } else {
                mMethod.invoke(obj, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean needDeepClone() {
        return false;
    }
}
