package com.nuts.lib.controller;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public final class VoidReturn extends Return<Void> {
    public VoidReturn() {
        super(null);
    }

    public VoidReturn(final Callable<Object> callable, final Method method) {
        super(callable, method);
    }
}
