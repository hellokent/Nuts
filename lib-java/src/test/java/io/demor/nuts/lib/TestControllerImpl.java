package io.demor.nuts.lib;

import java.util.concurrent.TimeUnit;

import io.demor.nuts.lib.controller.BaseController;
import io.demor.nuts.lib.controller.ExceptionWrapper;
import io.demor.nuts.lib.controller.Return;
import io.demor.nuts.lib.storage.MemoryEngine;
import io.demor.nuts.lib.storage.Storage;
import io.demor.nuts.sample.lib.controller.DemoException;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.nuts.sample.lib.event.SimpleListener;
import io.demor.nuts.sample.lib.event.TestEvent;
import io.demor.nuts.sample.lib.module.SimpleObject;

public class TestControllerImpl extends BaseController implements TestController {
    public static final TestController IMPL = new TestControllerImpl();
    private static SimpleListener sSimpleListener = MockApp.sListenerBus.provide(SimpleListener.class);
    private static Storage<SimpleObject> sStorage = new Storage.Builder<SimpleObject>()
            .setClass(SimpleObject.class)
            .setStorageEngine(new MemoryEngine())
            .build();
    private int mCount;

    @Override
    public Return<Integer> get() {
        return of(mCount);
    }

    @Override
    public Return<String> add(final int count) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            mCount += count;
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        MockApp.sEventBus.post(new TestEvent(String.valueOf(mCount)));
        return ofVoid();
    }

    @Override
    public Return<Void> callListenerInt(final int count) {
        sSimpleListener.onGotInt(count);
        return ofVoid();
    }

    @Override
    public Return<Void> callListenerString(final String msg) {
        sSimpleListener.onGotString(msg);
        return ofVoid();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public SimpleObject getStorage() {
        return sStorage.get();
    }
}
