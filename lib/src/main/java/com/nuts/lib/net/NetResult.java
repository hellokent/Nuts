package com.nuts.lib.net;

class NetResult {

    public IResponse mIResponse;

    public boolean mIsSuccess;

    public NetResult(final IResponse IResponse, final boolean isSuccess) {
        mIResponse = IResponse;
        mIsSuccess = isSuccess;
    }

    static NetResult ofSuccess(IResponse iResponse) {
        return new NetResult(iResponse, true);
    }

    static NetResult ofFailed(IResponse iResponse) {
        return new NetResult(iResponse, false);
    }
}
