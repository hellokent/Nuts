package com.nuts.sample.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.nuts.sample.R;

public final class Dialogs {

    public static Dialog createLoadingDialog(final Context context) {
        return createLoadingDialog(context, R.string.dialog_loading);
    }

    public static Dialog createLoadingDialog(final Context context, final int strId) {
        return createLoadingDialog(context, context.getString(strId));
    }

    public static Dialog createLoadingDialog(final Context context, final String str) {
        final ProgressDialog waitDialog = new ProgressDialog(context);
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setMessage(str);
        waitDialog.setCancelable(false);
        waitDialog.setCanceledOnTouchOutside(false);
        waitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
            }
        });
        return waitDialog;
    }

}
