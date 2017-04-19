package io.demor.nuts.lib.controller;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.view.View;
import com.google.common.collect.Maps;
import io.demor.nuts.lib.Globals;
import io.demor.nuts.lib.ReflectUtils;
import io.demor.nuts.lib.annotation.controller.CheckActivity;
import io.demor.nuts.lib.task.SafeTask;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.*;

class ReturnImpl<T> extends Return<T> implements Globals {

    private static final HashMap<Class, Field> CONTEXT_FIELD_MAP = Maps.newHashMap();
    private static final Class[] CHECK_CLASSES = {Activity.class, Fragment.class, View.class};
    private final boolean mCreatedByConstructor;
    private volatile ControllerCallback<T> mCallback;
    private volatile boolean mStarted;
    private volatile boolean mEnded;
    private boolean mNeedCheckActivity = false;
    private Activity mActivity;
    private Future<T> mFuture;
    private Throwable mWrappedException;
    private Throwable mHappenedThrowable;
    private final Runnable mResultRunnable = new Runnable() {
        @Override
        public void run() {
            if (exceptionHappened()) {
                mCallback.onException(mHappenedThrowable);
            } else {
                mCallback.onResult(mData);
            }
        }
    };
    private Date mBeginTime, mEndTime;
    private long mTimeoutMillis = -1;
    private TimeoutListener mTimeoutListener;

    ReturnImpl(final T data) {
        super(data);
        mData = data;
        mCreatedByConstructor = true;
    }

    ReturnImpl(final Callable<Object> callable, Method method) {
        super(callable, method);
        mCreatedByConstructor = false;
        mNeedCheckActivity = method.getAnnotation(CheckActivity.class) != null;
        Callable<T> wrappedCallable = new Callable<T>() {
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
                        mData = (T) ((ReturnImpl) o).mData;
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
        };

        String tag = TagCache.getTag(mMethod);
        if (TagCache.NO_TAG.equals(tag)) {
            mFuture = SafeTask.THREAD_POOL_EXECUTOR.submit(wrappedCallable);
        } else {
            mFuture = TagCache.run(tag, wrappedCallable);
        }
    }

    @Override
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

    @Override
    public final void asyncUI(final ControllerCallback<T> callback) {
        asyncUIWithDialog(callback, null);
    }

    public final void asyncUIWithDialog(final ControllerCallback<T> callback, final Dialog dialog) {
        updateCallbackFieldCache(callback);
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

    @Override
    public Return<T> setNeedCheckActivity(final boolean needCheckActivity) {
        mNeedCheckActivity = needCheckActivity;
        return this;
    }

    @Override
    public Return<T> addListener(final ControllerListener<T> listener) {
        super.addListener(listener);
        Globals.UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mStarted) {
                    listener.onPrepare();
                }
                if (mEnded) {
                    if (mHappenedThrowable == null) {
                        listener.onInvoke(mData);
                    } else {
                        listener.onThrow(mHappenedThrowable);
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
                callOnBegin();
                mStarted = true;
            }
        });
    }

    private void performLiftCircleEnd(final T data) {
        postThenWait(new Runnable() {
            @Override
            public void run() {
                callOnEnd(data);
                mEnded = true;
            }
        });
    }


    private void performLiftCircleException(final Throwable throwable) {
        mHappenedThrowable = throwable;
        postThenWait(new Runnable() {
            @Override
            public void run() {
                callOnException(throwable);
            }
        });
    }


    private boolean validateReturn() {
        if (isTimeout() && !mTimeoutListener.onTimeout(mBeginTime, mEndTime)) {
            return false;
        }

        return !(mCallback == null || isActivityFinishing());
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

        final Field f = CONTEXT_FIELD_MAP.get(mCallback.getClass());
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

    private void updateCallbackFieldCache(final ControllerCallback<T> controllerCallback) {
        final Class<?> clz = controllerCallback.getClass();
        if (!CONTEXT_FIELD_MAP.containsKey(clz)) {
            for (Field f : getClass().getDeclaredFields()) {
                final Class<?> fieldClz = f.getClass();
                for (Class c : CHECK_CLASSES) {
                    if (ReflectUtils.isSubclassOf(fieldClz, c)) {
                        CONTEXT_FIELD_MAP.put(clz, f);
                        f.setAccessible(true);
                        break;
                    }
                }
                if (CONTEXT_FIELD_MAP.containsKey(clz)) {
                    break;
                }
            }
        }
    }
}