package io.demor.nuts.lib.controller;

import android.test.AndroidTestCase;
import io.demor.nuts.lib.TestUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ControllerVoidTestCase extends AndroidTestCase {
    VoidTestController mController = new ControllerInvokeHandler<>(VoidTestController.IMPL).createProxy();

    public void testAsyncRun() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        mController.load()
                .asyncUI(new ControllerCallback<Void>() {
                    @Override
                    public void onResult(final Void aVoid) {
                        latch.countDown();
                    }
                });
        latch.await(3, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
    }

    public void testSyncRun() throws Exception {
        Object o = mController.load()
                .sync();
        assertNull(o);
    }

    public void testRunBg() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        mController.loadBg(latch);
        latch.await(3, TimeUnit.SECONDS);
        assertEquals(1, latch.getCount());
    }

    public void testMultiTask() throws Exception {
        final int count = 500;
        final CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; ++i) {
            mController.run(latch, i)
                    .asyncUI(new ControllerCallback<Void>() {
                        @Override
                        public void onResult(final Void aVoid) {
                            assertTrue(TestUtil.inUIThread());
                        }
                    });
        }
        latch.await();
        assertEquals(0, latch.getCount());
    }

}
