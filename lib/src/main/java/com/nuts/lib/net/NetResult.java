package com.nuts.lib.net;

import java.util.HashMap;

import com.google.common.collect.Maps;

public class NetResult {

    public IResponse mIResponse;

    public boolean mIsSuccess;

    public int mStatusCode;

    public String mStrResult;

    public HashMap<String, String> mHeader = Maps.newHashMap();

    public Exception mException;
}
