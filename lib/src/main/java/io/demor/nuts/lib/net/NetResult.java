package io.demor.nuts.lib.net;

import com.google.common.collect.Maps;

import java.util.HashMap;

public class NetResult {

    public IResponse mIResponse;

    public boolean mIsSuccess;

    public int mStatusCode;

    public byte[] mResult;

    public HashMap<String, String> mHeader = Maps.newHashMap();

    public Exception mException;
}
