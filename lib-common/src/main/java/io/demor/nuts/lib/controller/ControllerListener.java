package io.demor.nuts.lib.controller;

public interface ControllerListener {
    void onBegin();

    void onEnd(Object response);

    void onException(Throwable throwable);
}
