package com.nuts.lib.controller;

import android.util.Pair;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Created by 陈阳(chenyang@edaijia-staff.cn>)
 * Date: 7/2/14 3:08 PM.
 */
public class PairReturn<F, S> extends Return<Pair<F, S>> {

    public PairReturn(F f, S s) {
        super(Pair.create(f, s));
    }

    public PairReturn(final Callable callable,final Method method) {
        super(callable, method);
    }

    public static <F, S> PairReturn<F, S> create(F first, S second) {
        return new PairReturn<>(first, second);
    }
}
