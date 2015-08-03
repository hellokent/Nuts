package com.nuts.lib.net;

class NetResult {

    public IResponse mIResponse;

    public boolean mIsSuccess;

    public int mStatusCode;

    public NetResult(final IResponse IResponse, final boolean isSuccess, final int statusCode) {
        mIResponse = IResponse;
        mIsSuccess = isSuccess;
        mStatusCode = statusCode;
    }
}
