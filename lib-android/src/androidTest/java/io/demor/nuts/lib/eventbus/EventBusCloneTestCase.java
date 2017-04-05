package io.demor.nuts.lib.eventbus;

import android.test.AndroidTestCase;

import io.demor.nuts.lib.annotation.eventbus.DeepClone;
import io.demor.nuts.lib.annotation.eventbus.Event;

import static io.demor.nuts.lib.Globals.BG_EXECUTOR;
import static io.demor.nuts.lib.Globals.UI_EXECUTOR;

public class EventBusCloneTestCase extends AndroidTestCase {

    private EventBus mEventBus;

    private int mHashCode;

    private Exception mException;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mEventBus = new EventBus(BG_EXECUTOR, UI_EXECUTOR);
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
        copyEventToTestCase(event);
    }

    @Event(runOn = ThreadType.SOURCE)
    void onDirectEvent(DirectEvent event) {
        copyEventToTestCase(event);
    }

    private void copyEventToTestCase(ExceptionEvent event) {
        mHashCode = event.mHashCode;
        mException = event.mException;
    }


    private static class ExceptionEvent extends BaseEvent<Void> {

        int mHashCode;
        Exception mException;

        private ExceptionEvent() {
            super(null);
            mException = new Exception();
            mHashCode = mException.hashCode();
        }
    }

    @DeepClone
    private static class DeepCloneEvent extends ExceptionEvent {
        DeepCloneEvent() {
            super();
        }
    }

    private static class DirectEvent extends ExceptionEvent {
        DirectEvent() {
            super();
        }
    }
}
