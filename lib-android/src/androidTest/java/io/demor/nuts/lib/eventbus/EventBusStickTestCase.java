package io.demor.nuts.lib.eventbus;

import android.test.AndroidTestCase;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.demor.nuts.lib.Globals;
import io.demor.nuts.lib.TestUtil;
import io.demor.nuts.lib.annotation.eventbus.Event;

public class EventBusStickTestCase extends AndroidTestCase {

    CountDownLatch mLatch;

    EventBus mEventBus;

    public static int getEventMethodCount() {
        int result = 0;

        for (Method m : EventBusTestCase.class.getDeclaredMethods()) {
            if (m.getParameterTypes().length != 1) {
                continue;
            }
            if (m.getParameterTypes()[0] == TestEvent.class) {
                ++result;
            }
        }
        return result;
    }

    @Override
    public void setUp() throws Exception {
        mEventBus = new EventBus(Globals.UI_EXECUTOR, Globals.BG_EXECUTOR);
    }

    @Override
    public void tearDown() throws Exception {
        mEventBus.unregister(this);
    }

    public void testPostStickEvent() throws Exception {
        final int count = 10;
        mLatch = new CountDownLatch(count);

        for (int i = 0; i < count; ++i) {
            mEventBus.postStick(new TestEvent(10));
        }
        assertEquals(count, mEventBus.getStickyEventCount());
        mEventBus.register(this);

        mLatch.await(3, TimeUnit.SECONDS);
        assertEquals(0, mLatch.getCount());
        assertEquals(0, mEventBus.getStickyEventCount());
    }


    @Event(runOn = ThreadType.MAIN)
    public void onEvent(TestEvent event) {
        assertTrue(TestUtil.inUIThread());
        assertNotNull(event.getData());
        assertEquals(10, event.getData()
                .intValue());
        mLatch.countDown();
    }

}
