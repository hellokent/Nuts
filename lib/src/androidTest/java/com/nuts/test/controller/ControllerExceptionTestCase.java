package com.nuts.test.controller;

import android.test.AndroidTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.nuts.lib.controller.ControllerCallback;
import com.nuts.lib.controller.ControllerListener;
import com.nuts.lib.controller.ExceptionWrapper;
import com.nuts.lib.controller.ProxyInvokeHandler;
import com.nuts.lib.controller.VoidReturn;
import com.nuts.test.TestUtil;

public class ControllerExceptionTestCase extends AndroidTestCase {

    TestController mController = new ProxyInvokeHandler<>(TestController.IMPL).createProxy();

    public void testAsyncWrappedException() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        final CountDownLatch latch2 = new CountDownLatch(1);
        mController.runThrowWrappedException()
                .addListener(new ControllerListener<Void>() {
                    @Override
                    public void onBegin() {
                        latch.countDown();
                    }

                    @Override
                    public void onEnd(final Void response) {
                        latch.countDown();
                    }

                    @Override
                    public void onException(final Throwable throwable) {
                        assertNotNull(throwable);
                        throwable.printStackTrace();
                        assertTrue(throwable instanceof IllegalArgumentException);
                        latch.countDown();
                    }
                })
                .asyncUI(new ControllerCallback<Void>() {
                    @Override
                    public void onResult(final Void aVoid) {
                        latch2.countDown();
                    }

                    @Override
                    public void onException(final Throwable e) {
                        assertTrue(e instanceof IllegalArgumentException);
                        latch2.countDown();
                    }
                });

        latch.await(3, TimeUnit.SECONDS);
        latch2.await(3, TimeUnit.SECONDS);

        assertEquals(0, latch.getCount());
        assertEquals(0, latch2.getCount());
    }

    public void testSyncWrappedException() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        final CountDownLatch latch2 = new CountDownLatch(1);

        try {
            mController.runThrowWrappedException()
                    .addListener(new ControllerListener<Void>() {
                        @Override
                        public void onBegin() {
                            latch.countDown();
                        }

                        @Override
                        public void onEnd(final Void response) {
                            latch.countDown();
                        }

                        @Override
                        public void onException(final Throwable throwable) {
                            assertNotNull(throwable);
                            assertTrue(throwable instanceof IllegalArgumentException);
                            latch.countDown();
                        }
                    })
                    .sync();
            latch2.countDown();
        } catch (ExceptionWrapper wrapper) {
            assertNotNull(wrapper);
            assertTrue(wrapper.getCause() instanceof IllegalArgumentException);
            latch2.countDown();
        }

        latch.await(3, TimeUnit.SECONDS);
        latch2.await(3, TimeUnit.SECONDS);

        assertEquals(0, latch.getCount());
        assertEquals(0, latch2.getCount());
    }

    public void testSyncRuntimeException() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        try {
            mController.runThrowRuntimeException()
                    .addListener(new ControllerListener<Void>() {
                        @Override
                        public void onBegin() {
                            latch.countDown();
                        }

                        @Override
                        public void onEnd(final Void response) {
                            latch.countDown();
                        }

                        @Override
                        public void onException(final Throwable throwable) {
                            assertNotNull(throwable);
                            assertTrue(throwable instanceof NullPointerException);
                            latch.countDown();
                        }
                    })
                    .sync();
            latch2.countDown();
        } catch (ExceptionWrapper wrapper) {
            assertNotNull(wrapper);
            assertNotNull(wrapper.getCause());
            assertTrue(wrapper.getCause() instanceof NullPointerException);
            latch2.countDown();
        }

        latch.await(3, TimeUnit.SECONDS);
        latch2.await(3, TimeUnit.SECONDS);

        assertEquals(0, latch.getCount());
        assertEquals(0, latch2.getCount());
    }

    public void testAsyncRuntimeException() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        final CountDownLatch latch2 = new CountDownLatch(1);
        mController.runThrowRuntimeException()
                .addListener(new ControllerListener<Void>() {
                    @Override
                    public void onBegin() {
                        latch.countDown();
                    }

                    @Override
                    public void onEnd(final Void response) {
                        latch.countDown();
                    }

                    @Override
                    public void onException(final Throwable throwable) {
                        assertNotNull(throwable);
                        throwable.printStackTrace();
                        assertTrue(throwable instanceof NullPointerException);
                        latch.countDown();
                    }
                })
                .asyncUI(new ControllerCallback<Void>() {
                    @Override
                    public void onResult(final Void aVoid) {
                        latch2.countDown();
                    }

                    @Override
                    public void onException(final Throwable e) {
                        assertTrue(e instanceof NullPointerException);
                        latch2.countDown();
                    }
                });

        latch.await(3, TimeUnit.SECONDS);
        latch2.await(3, TimeUnit.SECONDS);

        assertEquals(0, latch.getCount());
        assertEquals(0, latch2.getCount());
    }

    public void testAsyncRuntimeException2() throws Exception {
        try {
            mController.runThrowRuntimeException()
                    .sync();
        } catch (ExceptionWrapper wrapper) {
            assertTrue(wrapper.getCause() instanceof NullPointerException);
            return;
        }
        assertTrue(false);
    }

    public void testAsyncRuntimeException3() throws Exception {
        final int count = 5;
        final CountDownLatch latch = new CountDownLatch(count);
        VoidReturn voidReturn = mController.runThrowRuntimeException();
        for (int i = 0; i < count; ++i) {
            Thread.sleep(100);
            voidReturn.addListener(new ControllerListener<Void>() {
                @Override
                public void onBegin() {

                }

                @Override
                public void onEnd(final Void response) {

                }

                @Override
                public void onException(final Throwable throwable) {
                    assertTrue(TestUtil.inUIThread());
                    assertTrue(throwable instanceof NullPointerException);
                    latch.countDown();
                }
            });
        }

        latch.await(3, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
    }
}
