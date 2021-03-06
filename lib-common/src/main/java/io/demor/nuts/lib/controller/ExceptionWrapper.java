package io.demor.nuts.lib.controller;

public class ExceptionWrapper extends RuntimeException {

    public ExceptionWrapper(final Throwable e) {
        super(e);
    }

    public void throwWrapped(final Throwable throwable) {
        throw new ExceptionWrapper(throwable);
    }
}
