package io.demor.nuts.lib.net;

import java.lang.reflect.Method;
import java.util.TreeMap;

public abstract class INet {
    protected abstract String onCreateUrl(String url, Method method, Object[] args);

    protected abstract void onCreateParams(TreeMap<String, String> params, TreeMap<String, String> headers, Method method, Object[] args);

    protected int getConnectionTimeout() {
        return 60;
    }

    protected int getWriteTimeout() {
        return 120;
    }

    protected int getReadTimeout() {
        return 120;
    }

    protected String getLogTag(String url, Method method, Object[] args) {
        return url;
    }
}
