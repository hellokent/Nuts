package com.nuts.test;

import android.os.Looper;

public final class TestUtil {
    private TestUtil() {
    }

    public static boolean inUIThread() {
        return Thread.currentThread() == Looper.getMainLooper()
                .getThread();
    }

}
