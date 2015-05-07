package com.nuts.lib.controller;

import android.app.Dialog;

/**
 * Created by demor on 10/9/14.
 */
public class ProxyDialogTask<T> extends ProxyTask<T> {
    final Dialog mDialog;

    public ProxyDialogTask(final ControllerCallback<T> callback, final boolean needCheckActivity, final Dialog dialog) {
        super(callback, needCheckActivity);
        mDialog = dialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    @Override
    protected void onPostExecute(final T t) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        super.onPostExecute(t);
    }
}
