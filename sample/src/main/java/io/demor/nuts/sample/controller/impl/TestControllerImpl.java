package io.demor.nuts.sample.controller.impl;

import io.demor.nuts.lib.controller.BaseController;
import io.demor.nuts.lib.controller.ExceptionWrapper;
import io.demor.nuts.lib.controller.Return;
import io.demor.nuts.lib.log.Logger;
import io.demor.nuts.lib.log.LoggerFactory;
import io.demor.nuts.sample.lib.controller.DemoException;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.nuts.sample.lib.event.TestEvent;
import io.demor.nuts.sample.lib.module.SimpleObject;

import java.util.concurrent.TimeUnit;

import static io.demor.nuts.lib.Globals.BUS;
import static io.demor.nuts.sample.config.Const.SIMPLE_LISTENER;
import static io.demor.nuts.sample.config.Const.SIMPLE_OBJECT_STORAGE;

public class TestControllerImpl extends BaseController implements TestController {

    static final Logger LOGGER = LoggerFactory.getLogger(TestControllerImpl.class);
    int mCount;

    @Override
    public Return<Integer> get() {
        return of(mCount);
    }

    @Override
    public Return<String> add(final int count) {
        LOGGER.v("add count:%s", count);
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            mCount += count;
        } catch (InterruptedException e) {
            LOGGER.exception(e);
        }
        return of("Count:" + mCount);
    }

    @Override
    public Return<String> addAll(int... count) {
        for (int i : count) {
            mCount += i;
        }
        return of("Count:" + mCount);
    }

    @Override
    public Return<Void> runCheckActivity() {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ofVoid();
    }

    @Override
    public Return<Void> runWithException() {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new ExceptionWrapper(new DemoException());
    }

    @Override
    public Return<Void> sendEvent() {
        BUS.post(new TestEvent(String.valueOf(mCount)));
        return ofVoid();
    }

    @Override
    public Return<Void> callListenerInt(final int count) {
        SIMPLE_LISTENER.onGotInt(count);
        return ofVoid();
    }

    @Override
    public Return<Void> callListenerString(final String msg) {
        SIMPLE_LISTENER.onGotString(msg);
        return ofVoid();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public SimpleObject getStorage() {
        return SIMPLE_OBJECT_STORAGE.get();
    }

    @Override
    public Return<Void> doForLongTime() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(100);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
