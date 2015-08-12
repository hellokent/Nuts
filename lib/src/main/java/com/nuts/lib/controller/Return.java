package com.nuts.lib.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.google.common.collect.Lists;
import com.nuts.lib.Globals;
import com.nuts.lib.ReflectUtils;
import com.nuts.lib.annotation.controller.CheckActivity;
import com.nuts.lib.task.SafeTask;

public class Return<T> implements Globals {

    final boolean mCreatedByConstructor;

    final List<ControllerListener<T>> mListeners = Lists.newCopyOnWriteArrayList();

    T mData;

    boolean mNeedCheckActivity = false;

    Activity mActivity;

    Method mMethod;

    volatile ControllerCallback<T> mCallback;

    Future<T> mFuture;

    Exception mWrappedException;

    Throwable mHappenedThrowable;

    volatile boolean mStarted;

    volatile boolean mEnded;

    public Return(final T data) {
        mData = data;
        mCreatedByConstructor = true;
    }

    public Return(final Callable<Object> callable, Method method) {
        mCreatedByConstructor = false;
        mNeedCheckActivity = method.getAnnotation(CheckActivity.class) != null;
        mMethod = method;

        mFuture = SafeTask.THREAD_POOL_EXECUTOR.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                if (mNeedCheckActivity && isActivityFinishing()) {
                    return null;
                }

                try {
                    performBegin();
                    Object o = callable.call();
                    if (o == null) {
                        return null;
                    } else if (ReflectUtils.isSubclassOf(o.getClass(), Return.class)) {
                        mData = (T) ((Return) o).mData;
                    } else {
                        mData = (T) o;
                    }
                    performEnd(mData);
                    return mData;
                } catch (final Exception e) {
                    performEnd(mData);
                    if (e.getCause() instanceof ExceptionWrapper) {
                        mWrappedException = ((ExceptionWrapper) e.getCause()).mException;
                    } else {
                        performException(e);
                    }
                    throw e;
                } finally {
                    UI_HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mNeedCheckActivity && isActivityFinishing()) {
                                return;
                            }

                            performResult(mCallback);
                        }
                    });
                }
            }
        });
    }

    public final T sync() {
        if (mCreatedByConstructor) {
            return mData;
        }
        try {
            mData = mFuture.get();

            if (mWrappedException != null) {
                throw mWrappedException;
            }
            return mData;
        } catch (Exception e) {
            if (mWrappedException != null) {
                throw new ExceptionWrapper(mWrappedException);
            }
            e.printStackTrace();
            final Type returnType = mMethod.getGenericReturnType();
            if (mData instanceof Boolean || ReflectUtils.checkGenericType(returnType, Boolean.class) || ReflectUtils
                    .checkGenericType(returnType, boolean.class)) {
                return (T) Boolean.FALSE;
            }
            throw new Error(e);
        }
    }

    public final void asyncUI(final ControllerCallback<T> callback) {
        asyncUIWithDialog(callback, null);
    }

    public final void asyncUIWithDialog(final ControllerCallback<T> callback, final Dialog dialog) {
        if (dialog != null) {
            addListener(new DialogListenerImpl<T>(dialog));
        }
        if (mFuture.isDone()) {
            UI_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    performResult(callback);
                }
            });
        } else {
            mCallback = callback;

            if (mNeedCheckActivity) {
                try {
                    mActivity = getContext(mCallback);
                } catch (Exception e) {
                    mActivity = null;
                }
            }
        }
    }

    public Return<T> setNeedCheckActivity(final boolean needCheckActivity) {
        mNeedCheckActivity = needCheckActivity;
        return this;
    }

    public Return<T> addListener(final ControllerListener<T> listener) {
        mListeners.add(listener);
        if (mStarted) {
            listener.onBegin();
        }
        if (mEnded) { //TODO 异常处理不合适
            if (mHappenedThrowable == null) {
                listener.onEnd(mData);
            } else {
                listener.onException(mHappenedThrowable);
            }
        }
        return this;
    }

    private void performBegin() {
        for (ControllerListener<T> l : mListeners) {
            l.onBegin();
        }
        mStarted = true;
    }

    private void performEnd(T data) {
        for (ControllerListener<T> l : mListeners) {
            l.onEnd(data);
        }
        mEnded = true;
    }

    private void performException(Throwable throwable) {
        for (ControllerListener<T> l : mListeners) {
            l.onException(throwable);
        }
        mHappenedThrowable = throwable;
    }

    private void performResult(ControllerCallback<T> callback) {
        if (callback == null) {
            return;
        }
        if (mWrappedException == null) {
            callback.onResult(mData);
        } else {
            callback.handleException(mWrappedException);
        }
    }

    private boolean isActivityFinishing() {
        return mActivity != null && mActivity.isFinishing();
    }

    private Activity getContext(Object o) throws Exception {
        if (o == null) {
            return null;
        }

        final Field f = mCallback.getClass()
                .getDeclaredField("this$0");
        f.setAccessible(true);
        final Object outer = f.get(mCallback);

        if (outer == o) {
            return null;
        }

        Context c = null;
        if (outer instanceof View) {
            c = ((View) outer).getContext();
        } else if (outer.getClass()
                .getName()
                .contains("Fragment")) {
            c = (Context) outer.getClass()
                    .getMethod("getActivity")
                    .invoke(outer);
        } else if (outer instanceof Activity) {
            return (Activity) outer;
        }

        if (c != null && c instanceof Activity) {
            return (Activity) c;
        } else {
            return getContext(outer);
        }
    }
}