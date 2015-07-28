package com.nuts.lib.controller;

public class ExceptionWrapper extends RuntimeException {

    Exception mException;

    public ExceptionWrapper(final Exception e) {
        super();
        mException = e;
    }
}
