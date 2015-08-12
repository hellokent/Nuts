package io.demor.nuts.lib.controller;

public class BaseController {

    public static VoidReturn ofVoid() {
        return new VoidReturn();
    }

    public static <R> Return<R> of(R r) {
        return new Return<>(r);
    }
}
