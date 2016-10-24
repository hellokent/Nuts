package io.demor.nuts.lib.controller;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public abstract class VoidReturn extends Return<Void> {
    public VoidReturn(Void data) {
        super(data);
    }

    public VoidReturn(Callable<Object> callable, Method method) {
        super(callable, method);
    }
}
