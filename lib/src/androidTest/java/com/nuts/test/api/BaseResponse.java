package com.nuts.test.api;

import com.nuts.lib.net.IResponse;

public class BaseResponse implements IResponse {

    public int code = 0;

    public String msg = "hello";

    private int mStatusCode;

    private int mErrorCode;

    public int getStatusCode() {
        return mStatusCode;
    }

    @Override
    public void setStatusCode(final int statusCode) {
        mStatusCode = statusCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    @Override
    public void setErrorCode(final int errorCode) {
        mErrorCode = errorCode;
    }
}
