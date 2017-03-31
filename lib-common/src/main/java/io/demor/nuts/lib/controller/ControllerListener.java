package io.demor.nuts.lib.controller;

public interface ControllerListener<T> {
    void onPrepare();

    void onInvoke(T response);

    void onThrow(Throwable throwable);
}
