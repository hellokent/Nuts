package com.nuts.lib.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.google.common.collect.Lists;
import com.nuts.lib.Globals;
import com.nuts.lib.ReflectUtils;
import com.nuts.lib.task.SafeTask;

/**
 * Created by 陈阳(chenyang@edaijia-staff.cn>)
 * Date: 6/24/14 11:03 AM.
 */
public class Return<T> implements Globals {

    final boolean mCreatedByConstructor;

    final List<ControllerListener<T>> mListeners = Lists.newCopyOnWriteArrayList();

    T mData;

    boolean mNeedCheckActivity = false;

    Activity mActivity;

    Method mMethod;

    volatile ControllerCallback<T> mCallback;

    Future<T> mFuture;

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
                    performException(e);
                    throw e;
                } finally {
                    UI_HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mNeedCheckActivity && isActivityFinishing()) {
                                return;
                            }

                            if (mCallback != null) {
                                mCallback.onResult(mData);
                            }
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
            return mData;
        } catch (Exception e) {
            e.printStackTrace();
            if (mData instanceof Boolean || ReflectUtils.checkGenericType(mMethod.getGenericReturnType(), Boolean
                    .class)) {
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
                    callback.onResult(mData);
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
        return this;
    }

    private void performBegin() {
        for (ControllerListener<T> l : mListeners) {
            l.onBegin();
        }
    }

    private void performEnd(T data) {
        for (ControllerListener<T> l : mListeners) {
            l.onEnd(data);
        }
    }

    private void performException(Throwable throwable) {
        for (ControllerListener<T> l : mListeners) {
            l.onException(throwable);
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