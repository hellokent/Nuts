package io.demor.nuts.lib.eventbus;

public abstract class BaseEvent<T> {
    T mData;

    public BaseEvent(final T data) {
        mData = data;
    }

    public T getData() {
        return mData;
    }
}
