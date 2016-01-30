package io.demor.nuts.lib.net;

import java.lang.reflect.Method;

public abstract class INet {
    protected abstract void handleRequest(ApiRequest request, Method method, Object[] args);

    protected int getConnectionTimeout() {
        return 60;
    }

    protected int getWriteTimeout() {
        return 120;
    }

    protected int getReadTimeout() {
        return 120;
    }

    public abstract Object createResponse(Class clz, ApiResponse response);
}
