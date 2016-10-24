package io.demor.nuts.lib.controller;

public abstract class ControllerCallback<T> {

    public abstract void onResult(T t);

    public void onException(Throwable e) {
    }
}
