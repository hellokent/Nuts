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
        if (timeout <= 0) {
            return null;
        }
        try {
            long startTime = System.currentTimeMillis();
            PushObject o = PUSH_QUEUE.poll(timeout, unit);
            if (o == null) {
                return null;
            } else if (mClz.getName().equals(o.mDataClz)) {
                return (T) o.mData;
            } else {
                return waitForSingleEvent(unit.toMillis(timeout) - (System.currentTimeMillis() - startTime), TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<T> waitForAllEvent(long timeout, TimeUnit unit) {
        final List<T> list = Lists.newArrayList();
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < unit.toMillis(timeout)) {
            try {
                final PushObject o = PUSH_QUEUE.poll(unit.toMillis(timeout) - (System.currentTimeMillis() - startTime), TimeUnit.MILLISECONDS);
                if (o != null && mClz.getName().equals(o.mDataClz)) {
                    list.add((T) o.mData);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
