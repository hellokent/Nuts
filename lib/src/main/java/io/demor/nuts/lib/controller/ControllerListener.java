package io.demor.nuts.lib.controller;

public interface ControllerListener<T> {
    void onBegin();

    void onEnd(T response);

    void onException(Throwable throwable);
}
