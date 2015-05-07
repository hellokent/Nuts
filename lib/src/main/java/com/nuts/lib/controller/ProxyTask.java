package com.nuts.lib.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

import com.nuts.lib.Globals;
import com.nuts.lib.task.SafeTask;

/**
 * Created by demor on 10/9/14.
 */
public class ProxyTask<T> extends SafeTask<Return<T>, T> {
    final ControllerCallback<T> mCallback;
    Activity mActivity;

    public ProxyTask(final ControllerCallback<T> callback, final boolean needCheckActivity) {
        mCallback = callback;
        if (needCheckActivity) {
            try {
                mActivity = getContext(callback);
            } catch (Exception e) {
                mActivity = null;
            }
        } else {
            mActivity = null;
        }
    }

    @Override
    protected T doInBackground(final Return<T>... params) {
        if (isActivityFinishing()) {
            cancel(false);
            return null;
        }
        return params[0].sync();
    }

    @Override
    protected void onPostExecute(final T t) {
        if (isActivityFinishing()) {
            return;
        }

        if (mCallback != null) {
            mCallback.onResult(t);
        }
    }

    public void executeDelayed(final long delayedInMillis, final Return<T>... returns) {
        Globals.UI_HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                execute(returns);
            }
        }, delayedInMillis);
    }

    protected final boolean isActivityFinishing() {
        return mActivity != null && mActivity.isFinishing();
    }

    Activity getContext(Object o) throws Exception {
        if (o == null) {
            return null;
        }

        Log.v("controller", "path:" + o);
        final Field f = mCallback.getClass().getDeclaredField("this$0");
        f.setAccessible(true);
        Object outer = f.get(mCallback);


        Context c = null;
        if (outer instanceof View) {
            c = ((View) outer).getContext();
        } else if (outer.getClass().getName().contains("Fragment")) {
            c = (Context) outer.getClass().getMethod("getActivity").invoke(outer);
        } else if (outer instanceof Activity) {
            return  (Activity) outer;
        }

        if (c != null && c instanceof Activity) {
            return (Activity) c;
        } else {
            return getContext(outer);
        }
    }
}
