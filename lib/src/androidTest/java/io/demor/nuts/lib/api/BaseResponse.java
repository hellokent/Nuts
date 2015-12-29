package io.demor.nuts.lib.api;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

public class BaseResponse {

    public static final int BAD_NETWORK = -1;
    public static final int SUCCESS = 0;
    public static final int ILLEGAL_JSON = -2;
    public int code = BAD_NETWORK;
    public String msg = "hello";

    private HashMap<String, String> mHeader;

    @Expose(serialize = false, deserialize = false)
    private int mStatusCode;

    @Expose(serialize = false, deserialize = false)
    private int mErrorCode;

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int errorCode) {
        mErrorCode = errorCode;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int statusCode) {
        mStatusCode = statusCode;
    }

    public HashMap<String, String> getHeader() {
        return mHeader;
    }

    public void setHeader(HashMap<String, String> header) {
        mHeader = header;
    }

    public String getHeader(String key) {
        return mHeader.get(key);
    }
}
