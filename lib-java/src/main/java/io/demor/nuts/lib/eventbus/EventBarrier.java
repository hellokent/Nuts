package io.demor.nuts.lib.eventbus;

import com.google.common.collect.Lists;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.module.PushObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventBarrier<T extends BaseEvent> extends BaseBarrier {

    final Class<T> mClz;

    public EventBarrier(final AppInstance appInstance, final Class<T> clz) throws Exception {
        super(appInstance);
        mClz = clz;
    }

    public T waitForSingleEvent(long timeout, TimeUnit unit) {
        PushObject o = waitForSingle(timeout, unit, new PushFilter() {
            @Override
            public boolean checkPush(final PushObject object) {
                return object.mType == PushObject.TYPE_EVENT && mClz.getName().equals(object.mDataClz);
            }
        });
        return o == null ? null : (T) o.mData;
    }

    public List<T> waitForAllEvent(long timeout, TimeUnit unit) {
        final List<T> list = Lists.newArrayList();
        waitForAll(timeout, unit, new PushHandler() {
            @Override
            public void onReceivePush(final PushObject o) {
                if (o != null && o.mType == PushObject.TYPE_EVENT && mClz.getName().equals(o.mDataClz)) {
                    list.add((T) o.mData);
                }
            }
        });
        return list;
    }
}
