package io.demor.nuts.lib.controller;

public class BaseController {
    public static Return<Void> ofVoid() {
        return new ReturnImpl<>(null);
    }

    public static <R> Return<R> of(R r) {
        return new ReturnImpl<>(r);
    }

}
