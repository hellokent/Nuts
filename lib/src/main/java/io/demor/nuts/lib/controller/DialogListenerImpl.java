package io.demor.nuts.lib.controller;

import android.app.Dialog;
import io.demor.nuts.lib.Globals;

public class DialogListenerImpl implements ControllerListener, Globals {

    final Dialog mDialog;

    public DialogListenerImpl(final Dialog dialog) {
        mDialog = dialog;
    }

    @Override
    public void onBegin() {
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
    public void onEnd(final Object response) {
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
    public void onException(final Throwable throwable) {

    }
}
