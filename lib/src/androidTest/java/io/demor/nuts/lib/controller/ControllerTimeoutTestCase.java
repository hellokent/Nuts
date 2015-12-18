package io.demor.nuts.lib.controller;

import android.test.AndroidTestCase;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ControllerTimeoutTestCase extends AndroidTestCase {

    TestController mController = new ProxyInvokeHandler<>(TestController.IMPL).createProxy();

    public void testStopCallback() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);

        mController.sleep(5)
                .setTimeout(1, TimeUnit.SECONDS, new TimeoutListener() {
                    @Override
                    public boolean onTimeout(final Date startTime, final Date stopTime) {
                        latch.countDown();
                        return false;
                    }
                })
                .asyncUI(new ControllerCallback<Void>() {
                    @Override
                    public void onResult(final Void aVoid) {
                        latch.countDown();
                    }
                });

        latch.await();
        assertEquals(0, latch.getCount());
    }

    public void testContinueCallback() throws Exception {

        final CountDownLatch latch = new CountDownLatch(2);

        mController.sleep(5)
                .setTimeout(1, TimeUnit.SECONDS, new TimeoutListener() {
                    @Override
                    public boolean onTimeout(final Date startTime, final Date stopTime) {
                        latch.countDown();
                        return true;
                    }
                })
                .asyncUI(new ControllerCallback<Void>() {
                    @Override
                    public void onResult(final Void aVoid) {
                        latch.countDown();
                    }
                });

        latch.await(10, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
    }

    public void testNoTimeout() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        mController.sleep(1)
                .setTimeout(5, TimeUnit.SECONDS, new TimeoutListener() {
                    @Override
                    public boolean onTimeout(final Date startTime, final Date stopTime) {
                        latch.countDown();
                        return true;
                    }
                })
                .asyncUI(new ControllerCallback<Void>() {
                    @Override
                    public void onResult(final Void aVoid) {
                        latch.countDown();
                    }
                });

        latch.await(3, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
    }
}
