package io.demor.nuts.lib.controller;

public class BaseController {

    public static VoidReturn ofVoid() {
        return new VoidReturnImpl();
    }

    public static <R> Return<R> of(R r) {
        return new ReturnImpl<R>(r);
    }
}
