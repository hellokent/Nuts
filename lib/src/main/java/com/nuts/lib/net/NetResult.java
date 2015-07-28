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

    static NetResult ofSuccess(IResponse iResponse) {
        return new NetResult(iResponse, true, 200);
    }

    static NetResult ofFailed(IResponse iResponse, int statusCode) {
        return new NetResult(iResponse, false, statusCode);
    }
}
