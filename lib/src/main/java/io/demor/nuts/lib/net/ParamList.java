package io.demor.nuts.lib.net;

import com.google.common.collect.Maps;

import java.util.Map;

public class ParamList {

    Map<String, String> mData = Maps.newLinkedHashMap();
    private boolean mSkipNullValue = true;
    private String mNullValue;

    private ParamList() {
    }

    public void add(String key, Object value) {
        if (value == null) {
            if (mSkipNullValue) {
                return;
            } else {
                value = mNullValue;
            }
        }
        mData.put(key, value.toString());
    }

    public Map<String, String> getData() {
        return mData;
    }

    public static class Builder {

        ParamList mResult;

        public Builder() {
            mResult = new ParamList();
        }

        public Builder skipNullValue(boolean skip) {
            mResult.mSkipNullValue = skip;
            return this;
        }

        public Builder nullValue(String value) {
            if (value == null && !mResult.mSkipNullValue) {
                throw new IllegalArgumentException("null set null param and cannot skip");
            }
            mResult.mNullValue = value;
            return this;
        }

        public ParamList build() {
            return mResult;
        }
    }
}
