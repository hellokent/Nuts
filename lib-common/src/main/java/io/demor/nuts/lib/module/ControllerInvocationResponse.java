package io.demor.nuts.lib.module;

public class ControllerInvocationResponse extends BaseResponse {
    public String mData;

    public ControllerInvocationResponse() {
    }

    public ControllerInvocationResponse(final String data) {
        mData = data;
    }
}
