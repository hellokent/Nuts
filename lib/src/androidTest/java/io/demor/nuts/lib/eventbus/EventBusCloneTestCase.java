package io.demor.nuts.lib.eventbus;

import android.test.AndroidTestCase;
import io.demor.nuts.lib.annotation.eventbus.DeepClone;
import io.demor.nuts.lib.annotation.eventbus.Event;

public class EventBusCloneTestCase extends AndroidTestCase {

    EventBus mEventBus;

    int mHashCode;

    Exception mException;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mEventBus = new EventBus();
        mEventBus.register(this);
    }

    public void testDeepClone() throws Exception {
        mEventBus.post(new DeepCloneEvent());
        assertTrue(mHashCode != mException.hashCode());
    }

    public void testDirect() throws Exception {
        mEventBus.post(new DirectEvent());
        assertTrue(mHashCode == mException.hashCode());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        mEventBus.unregister(this);
        mHashCode = -1;
        mException = null;
    }

    @Event(runOn = ThreadType.SOURCE)
    void onDeepCloneEvent(DeepCloneEvent event) {
        mHashCode = event.mHashCode;
        mException = event.mException;
    }

    @Event(runOn = ThreadType.SOURCE)
    void onDirectEvent(DirectEvent event) {
        mHashCode = event.mHashCode;
        mException = event.mException;
    }

    @DeepClone
    static class DeepCloneEvent extends BaseEvent<Void> {

        int mHashCode;

        Exception mException;

        DeepCloneEvent() {
            super(null);
            mException = new Exception();
            mHashCode = mException.hashCode();
        }
    }

    static class DirectEvent extends BaseEvent<Void> {

        int mHashCode;

        Exception mException;

        DirectEvent() {
            super(null);
            mException = new Exception();
            mHashCode = mException.hashCode();
        }
    }
}
