package com.nuts.lib.controller;

import android.app.Dialog;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import com.nuts.lib.BuildConfig;
import com.nuts.lib.Globals;
import com.nuts.lib.ReflectUtils;

/**
 * Created by 陈阳(chenyang@edaijia-staff.cn>)
 * Date: 6/24/14 11:03 AM.
 */
public class Return<T> implements Globals {

    final boolean mCreatedByConstructor;
    T mData;
    boolean mHasInvoked; //sync或者async被调用过一次
    boolean mNeedCheckActivity = false;
    Callable<Object> mObjectCallable;
    Method mMethod;

    public Return(final T data) {
        mData = data;
        mHasInvoked = true; //实现类调用，不予处理
        mCreatedByConstructor = true;
    }

    public Return(Callable<Object> callable, Method method) {
        mHasInvoked = false;
        mCreatedByConstructor = false;
        mObjectCallable = callable;
        mNeedCheckActivity = method.getAnnotation(CheckActivity.class) != null;
        mMethod = method;
    }

    public final T sync() {
        return mCreatedByConstructor ? mData : $call();
    }

    public final void asyncUI(final ControllerCallback<T> callback, final long delay) {
        new ProxyTask<>(callback, mNeedCheckActivity).executeDelayed(delay, this);
    }

    public final void asyncUI(final ControllerCallback<T> callback) {
        asyncUIWithDialog(callback, null);
    }

    public final void asyncUIWithDialog(final ControllerCallback<T> callback, final Dialog dialog) {
        new ProxyDialogTask<>(callback, mNeedCheckActivity, dialog).safeExecute(this);
    }

    public final void async() {
        asyncUI(null, -1);
    }

    private T $call() {
        mHasInvoked = true;
        try {
            Object o = mObjectCallable.call();
            if (o == null) {
                return null;
            } else if (ReflectUtils.isSubclassOf(o.getClass(), Return.class)) {
                mData = (T) ((Return) o).mData;
            } else {
                mData = (T) o;
            }
            return mData;
        } catch (Exception e) {
            e.printStackTrace();
            if (mData instanceof Boolean || ReflectUtils.checkGenericType(mMethod.getGenericReturnType(), Boolean.class)) {
                return (T) Boolean.FALSE;
            }
            throw new Error(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (!mHasInvoked && BuildConfig.DEBUG) {
            throw new Error(String.format("controller:%s, 还没有任何调用", mObjectCallable));
        }
    }

}
