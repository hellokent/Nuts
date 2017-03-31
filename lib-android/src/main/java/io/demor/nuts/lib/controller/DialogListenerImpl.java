package io.demor.nuts.lib.controller;

import android.app.Dialog;
import io.demor.nuts.lib.Globals;

public class DialogListenerImpl<T> implements ControllerListener<T>, Globals {

    final Dialog mDialog;

    public DialogListenerImpl(final Dialog dialog) {
        mDialog = dialog;
    }

    @Override
    public void onPrepare() {
        UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
            }
        });
    }

    @Override
    public void onInvoke(final T response) {
        UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onThrow(final Throwable throwable) {

    }
}
