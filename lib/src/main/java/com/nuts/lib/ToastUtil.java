package com.nuts.lib;

import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Toast util class.
 */
public class ToastUtil {

    private static Toast sToast = null;

    public static void showMessage(final String msg) {
        showMessage(msg, Toast.LENGTH_SHORT);
    }

    public static void showLongMessage(final String msg) {
        showMessage(msg, Toast.LENGTH_LONG);
    }

    public static void showMessage(final int msg) {
        showMessage(msg, Toast.LENGTH_SHORT);
    }

    public static void showMessage(final String msg,
                                   final int len) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            Globals.UI_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    showMessage(msg, len);
                }
            });
            return;
        }
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (sToast != null) {
            sToast.cancel();
        }
        sToast = Toast.makeText(BaseApplication.getGlobalContext(), msg, len);
        sToast.show();
    }

    public static void showMessage(final int msg,
                                   final int len) {
        showMessage(BaseApplication.getGlobalContext().getString(msg), len);
    }
}
