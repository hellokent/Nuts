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
        return null;
    }

    public List<T> waitForAllEvent(long timeout, TimeUnit unit) {
        final List<T> list = Lists.newArrayList();
        return list;
    }

    @Override
    protected void onReceiveData(final PushObject object) {

    }


}
