package io.demor.nuts.lib.api;

import com.google.common.collect.Maps;
import com.google.gson.annotations.Expose;
import io.demor.nuts.lib.net.IResponse;

import java.util.HashMap;
import java.util.Map;

public class BaseResponse implements IResponse {

    public int code = 0;

    public String msg = "hello";

    @Expose(serialize = false, deserialize = false)
    private int mStatusCode;

    @Expose(serialize = false, deserialize = false)
    private int mErrorCode;

    @Expose(serialize = false, deserialize = false)
    private HashMap<String, String> mHeader = Maps.newHashMap();

    public int getStatusCode() {
        return mStatusCode;
    }

    @Override
    public void setStatusCode(final int statusCode) {
        mStatusCode = statusCode;
    }

    @Override
    public void setHeader(final Map<String, String> header) {
        mHeader.putAll(header);
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    @Override
    public void setErrorCode(final int errorCode) {
        mErrorCode = errorCode;
    }

    public String getHeader(String key) {
        return mHeader.get(key);
    }
}
