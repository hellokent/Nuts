package com.nuts.lib.controller;

public abstract class ControllerCallback<T> {
    public abstract void onResult(T t);

    public void handleException(Exception e) {

    }
}
