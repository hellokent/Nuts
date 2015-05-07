package com.nuts.lib.controller;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Created by 陈阳(chenyang@edaijia-staff.cn>)
 * Date: 6/26/14 11:56 AM.
 */
public final class VoidReturn extends Return<Void> {
    public VoidReturn() {
        super(null);
    }

    public VoidReturn(final Callable<Object> callable, final Method method) {
        super(callable, method);
    }
}
