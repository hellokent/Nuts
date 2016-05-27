package io.demor.nuts.lib.controller;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.view.View;
import com.google.common.collect.Lists;
import io.demor.nuts.lib.Globals;
import io.demor.nuts.lib.annotation.controller.CheckActivity;
import io.demor.nuts.lib.task.SafeTask;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

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
    Throwable mWrappedException;
    Throwable mHappenedThrowable;
    final Runnable mResultRunnable = new Runnable() {
        @Override
        public void run() {
            if (exceptionHappened()) {
                mCallback.onException(mHappenedThrowable);
            } else {
                mCallback.onResult(mData);
            }
        }
    };
    Date mBeginTime, mEndTime;
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

                try {
                    performLiftCircleBegin();
                    mBeginTime = new Date();

                    final Object o = callable.call();
                    if (o == null) {
                        return null;
                    } else {
                        mData = (T) ((Return) o).mData;
                    }
                } catch (final Throwable e) {
                    Throwable t = e;
                    if (e instanceof InvocationTargetException) {
                        t = e.getCause();
                        if (t instanceof ExceptionWrapper) {
                            t = t.getCause();
                            mWrappedException = t;
                        }
                    }
                    performLiftCircleException(t);
                } finally {
                    mEndTime = new Date();
                    if (validateReturn()) {
                        postThenWait(mResultRunnable);
                    }
                    performLiftCircleEnd(mData);
                }

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
        if (mFuture.isDone() && validateReturn()) {
            UI_HANDLER.post(mResultRunnable);
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

    private void postThenWait(final Runnable runnable) {
        final CountDownLatch latch = new CountDownLatch(1);
        Globals.UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                runnable.run();
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException ignored) {
        }
    }

    private void performLiftCircleBegin() {
        postThenWait(new Runnable() {
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
        postThenWait(new Runnable() {
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
        postThenWait(new Runnable() {
            @Override
            public void run() {
                for (ControllerListener<T> l : mListeners) {
                    l.onException(throwable);
                }
            }
        });
    }

    private boolean validateReturn() {
        if (isTimeout() && !mTimeoutListener.onTimeout(mBeginTime, mEndTime)) {
            return false;
        }

        if (mCallback == null || isActivityFinishing()) {
            return false;
        }
        return true;
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

        final Field f = ControllerCallback.CONTEXT_FIELD_MAP.get(mCallback.getClass());
        if (f == null) {
            return null;
        }
        final Object outer = f.get(mCallback);

        if (outer == o) {
            return null;
        }

        Context c = null;
        if (outer instanceof View) {
            c = ((View) outer).getContext();
        } else if (outer instanceof Fragment) {
            c = ((Fragment) outer).getActivity();
        } else if (outer instanceof Activity) {
            return (Activity) outer;
        }

        if (c != null && c instanceof Activity) {
            return (Activity) c;
        } else {
            return null;
        }
    }
}