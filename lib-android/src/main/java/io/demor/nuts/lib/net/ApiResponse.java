package io.demor.nuts.lib.net;

import com.google.common.collect.Maps;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;

public final class ApiResponse {

    private boolean mIsSuccess;

    private int mStatusCode;

    private byte[] mResult;

    private HashMap<String, String> mHeader = Maps.newHashMap();

    private Exception mException;

    protected ApiResponse() {
    }

    protected static ApiResponse ofSuccess(Response response) throws IOException {
        try {
            final ApiResponse result = new ApiResponse();
            result.mIsSuccess = response.isSuccessful();
            result.mResult = response.body().bytes();
            HashMap<String, String> headerMap = Maps.newHashMap();
            for (String name : response.headers().names()) {
                headerMap.put(name, response.header(name));
            }
            result.mHeader.putAll(headerMap);
            result.mStatusCode = response.code();
            if (!result.isSuccess()) {
                result.mException = new IOException("http code:" + response.code());
            }
            return result;
        } finally {
            response.body().close();
        }
    }

    protected static ApiResponse ofFailed(Exception e) {
        final ApiResponse result = new ApiResponse();
        result.mIsSuccess = false;
        result.mException = e;
        result.mStatusCode = -1;
        return result;
    }

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public byte[] getResult() {
        return mResult;
    }

    public HashMap<String, String> getHeader() {
        return mHeader;
    }

    public Exception getException() {
        return mException;
    }
}
