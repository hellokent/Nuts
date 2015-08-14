package io.demor.nuts.lib.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import io.demor.nuts.lib.Globals;
import io.demor.nuts.lib.ReflectUtils;
import io.demor.nuts.lib.annotation.controller.CheckActivity;
import io.demor.nuts.lib.task.SafeTask;

public class Return<T> implements Globals {

    final boolean mCreatedByConstructor;

    final List<ControllerListener<T>> mListeners = Lists.newCopyOnWriteArrayList();

    volatile ControllerCallback<T> mCallback;

    volatile boolean mStarted;

    volatile boolean mEnded;

    T mData;

    boolean mNeedCheckActivity = false;

    Activity mActivity;

    Method mMethod;

    Future<T> mFuture;

    Exception mWrappedException;

    Throwable mHappenedThrowable;

    Date mBeginTime;

    Date mEndTime;

    long mTimeoutMillis = -1;

    TimeoutListener mTimeoutListener;

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
                if (isActivityFinishing()) {
                    return null;
                }

                performLiftCircleBegin();
                mBeginTime = new Date();

                try {
                    Object o = callable.call();
                    if (o == null) {
                        return null;
                    } else if (ReflectUtils.isSubclassOf(o.getClass(), Return.class)) {
                        mData = (T) ((Return) o).mData;
                    } else {
                        mData = (T) o;
                    }
                } catch (final Throwable e) {
                    if (e instanceof InvocationTargetException) {
                        final Throwable cause = e.getCause();
                        if (cause instanceof ExceptionWrapper) {
                            mWrappedException = (Exception) cause.getCause();
                            performLiftCircleException(cause.getCause());
                        } else {
                            performLiftCircleException(cause);
                        }
                    } else {
                        performLiftCircleException(e);
                    }
                } finally {
                    mEndTime = new Date();
                }

                performResult();
                performLiftCircleEnd(mData);
                return mData;
            }
        });
    }

    public final T sync() {
        if (mCreatedByConstructor) {
            return mData;
        }
        try {
            mData = mFuture.get();
            if (exceptionHappened()) {
                throw new ExceptionWrapper(getHappenedException());
            }
            return mData;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new ExceptionWrapper(e.getCause());
        }
    }

    public final void asyncUI(final ControllerCallback<T> callback) {
        asyncUIWithDialog(callback, null);
    }

    public final void asyncUIWithDialog(final ControllerCallback<T> callback, final Dialog dialog) {
        if (dialog != null) {
            addListener(new DialogListenerImpl<T>(dialog));
        }
        if (mNeedCheckActivity) {
            try {
                mActivity = getContext(mCallback);
            } catch (Exception e) {
                mActivity = null;
            }
        }
        mCallback = callback;
        if (mFuture.isDone()) {
            UI_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    performResult();
                }
            });
        }
    }

    public Return<T> setNeedCheckActivity(final boolean needCheckActivity) {
        mNeedCheckActivity = needCheckActivity;
        return this;
    }

    public Return<T> addListener(final ControllerListener<T> listener) {
        mListeners.add(listener);
        Globals.UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mStarted) {
                    listener.onBegin();
                }
                if (mEnded) {
                    if (mHappenedThrowable == null) {
                        listener.onEnd(mData);
                    } else {
                        listener.onException(mHappenedThrowable);
                    }
                }
            }
        });
        return this;
    }

    public Return<T> setTimeout(int time, TimeUnit unit, TimeoutListener listener) {
        mTimeoutMillis = unit.toMillis(time);
        mTimeoutListener = listener;
        return this;
    }

    private void performLiftCircleBegin() {
        Globals.UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                for (ControllerListener<T> l : mListeners) {
                    l.onBegin();
                }
                mStarted = true;
            }
        });
    }

    private void performLiftCircleEnd(final T data) {
        Globals.UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                for (ControllerListener<T> l : mListeners) {
                    l.onEnd(data);
                }
                mEnded = true;
            }
        });
    }

    private void performLiftCircleException(final Throwable throwable) {
        mHappenedThrowable = throwable;
        Globals.UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                for (ControllerListener<T> l : mListeners) {
                    l.onException(throwable);
                }
            }
        });
    }

    private void performResult() {
        if (isTimeout() && !mTimeoutListener.onTimeout(mBeginTime, mEndTime)) {
            return;
        }

        if (mCallback == null || isActivityFinishing()) {
            return;
        }

        UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (exceptionHappened()) {
                    mCallback.onException(mHappenedThrowable);
                } else {
                    mCallback.onResult(mData);
                }
            }
        });
    }

    private boolean isTimeout() {
        return mBeginTime != null && mEndTime != null && (mEndTime.getTime() - mBeginTime.getTime()) > mTimeoutMillis
                && mTimeoutListener != null;

    }

    private boolean exceptionHappened() {
        return mWrappedException != null || mHappenedThrowable != null;
    }

    private Throwable getHappenedException() {
        return mWrappedException == null ? mHappenedThrowable : mWrappedException;
    }

    private boolean isActivityFinishing() {
        return mNeedCheckActivity && mActivity != null && mActivity.isFinishing();
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