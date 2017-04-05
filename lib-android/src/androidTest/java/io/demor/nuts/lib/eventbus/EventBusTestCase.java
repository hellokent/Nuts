package io.demor.nuts.lib.eventbus;

import android.test.AndroidTestCase;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.demor.nuts.lib.Globals;
import io.demor.nuts.lib.TestUtil;
import io.demor.nuts.lib.annotation.eventbus.Event;
import io.demor.nuts.lib.task.RunnableTask;

public class EventBusTestCase extends AndroidTestCase {

    CountDownLatch mLatch;

    EventBus mEventBus;

    CountDownLatch mBgLatch;

    CountDownLatch mUILatch;


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
        mEventBus.register(this);
    }

    @Override
    public void tearDown() throws Exception {
        mEventBus.unregister(this);
    }

    public void testRun() throws Exception {
        final int count = 10;
        mLatch = new CountDownLatch(count * getEventMethodCount());

        for (int i = 0; i < count; ++i) {
            mEventBus.post(new TestEvent(10));
        }

        mLatch.await(10, TimeUnit.SECONDS);
        assertEquals(0, mLatch.getCount());
    }

    @Event(runOn = ThreadType.MAIN)
    public void onEvent(TestEvent event) {
        assertTrue(TestUtil.inUIThread());
        assertNotNull(event.getData());
        assertEquals(10, event.getData()
                .intValue());
        mLatch.countDown();
    }

    @Event(runOn = ThreadType.BACKGROUND)
    public void onEventBg(TestEvent event) {
        assertFalse(TestUtil.inUIThread());
        assertNotNull(event.getData());
        assertEquals(10, event.getData()
                .intValue());
        mLatch.countDown();
    }

    public void testSource() throws Exception {
        final int count = 50;
        mBgLatch = new CountDownLatch(count);
        mUILatch = new CountDownLatch(count);

        class PostRunnable implements Runnable {
            @Override
            public void run() {
                for (int i = 0; i < count; ++i) {
                    mEventBus.post(new TestSourceThreadEvent());
                }
            }
        }

        Globals.UI_HANDLER.post(new PostRunnable());

        new RunnableTask().safeExecute(new PostRunnable());

        mBgLatch.await(5, TimeUnit.SECONDS);
        mUILatch.await(5, TimeUnit.SECONDS);

        assertEquals(0, mBgLatch.getCount());
        assertEquals(0, mUILatch.getCount());

    }

    @Event(runOn = ThreadType.SOURCE)
    public void onEvent(TestSourceThreadEvent event) {
        if (TestUtil.inUIThread()) {
            mUILatch.countDown();
        } else {
            mBgLatch.countDown();
        }
    }
}
