package io.demor.nuts.lib.module;

import com.google.common.base.MoreObjects;

public class PushObject {

    public static final int TYPE_EVENT = 1;
    public static final int TYPE_LISTENER = 2;
    public static final int TYPE_LOG = 3;

    public int mType;

    public String mDataClz;

    public Object mData;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", mType)
                .add("dataClz", mDataClz)
                .add("data", mData)
                .omitNullValues()
                .toString();
    }
}
