package io.demor.nuts.lib.eventbus;

import io.demor.nuts.lib.storage.Storage;
import io.demor.nuts.lib.task.RunnableTask;

import java.lang.reflect.Method;

import static io.demor.nuts.lib.Globals.UI_HANDLER;

abstract class MethodContext {

    final Method mMethod;

    private final ThreadType mThreadType;

    MethodContext(final Method method, ThreadType threadType) {
        mMethod = method;
        mMethod.setAccessible(true);
        mThreadType = threadType;
    }

    void call(final Object obj, final Object... args) {
        switch (mThreadType) {
            case SOURCE:
                $call(obj, args);
                break;
            case MAIN:
                UI_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        $call(obj, args);
                    }
                });
                break;
            case BACKGROUND:
                new RunnableTask().safeExecute(new Runnable() {
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
