package com.nuts.lib.net;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

class NetResult {

    public IResponse mIResponse;

    public boolean mIsSuccess;

    public int mStatusCode;

    public String mStrResult;

    public Multimap<String, String> mHeader = ArrayListMultimap.create();
}
