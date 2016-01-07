package io.demor.nuts.lib.controller;

import android.test.AndroidTestCase;
import io.demor.nuts.lib.TestUtil;
import io.demor.nuts.lib.api.BaseResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ControllerTestCase extends AndroidTestCase {

    TestController mController = new ControllerInvokeHandler<>(TestController.IMPL).createProxy();

    public void testAsyncRun() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        mController.load()
                .asyncUI(new ControllerCallback<BaseResponse>() {
                    @Override
                    public void onResult(final BaseResponse baseResponse) {
                        assertTrue(TestUtil.inUIThread());
                        assertNotNull(baseResponse);
                        assertEquals("hello", baseResponse.msg);
                        latch.countDown();
                    }
                });
        latch.await(3, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
    }

    public void testSyncRun() throws Exception {
        BaseResponse response = mController.load()
                .sync();
        assertEquals("hello", response.msg);
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
            final int tmp = i;
            mController.run(latch, tmp)
                    .asyncUI(new ControllerCallback<Integer>() {
                        @Override
                        public void onResult(final Integer integer) {
                            assertTrue(TestUtil.inUIThread());
                            assertNotNull(integer);
                            assertEquals(tmp + 1, integer.intValue());
                        }
                    });
        }
        latch.await();
        assertEquals(0, latch.getCount());
    }

    public void testControllerLifeCircle() throws Exception {
        final CountDownLatch begin = new CountDownLatch(3);
        final CountDownLatch end = new CountDownLatch(3);

        class ListenerImpl implements ControllerListener<BaseResponse> {
            @Override
            public void onBegin() {
                assertTrue(TestUtil.inUIThread());
                begin.countDown();
            }

            @Override
            public void onEnd(final BaseResponse response) {
                assertTrue(TestUtil.inUIThread());
                assertNotNull(response);
                end.countDown();
            }

            @Override
            public void onException(final Throwable throwable) {
                assertTrue(TestUtil.inUIThread());
            }
        }

        mController.load()
                .addListener(new ListenerImpl())
                .addListener(new ListenerImpl())
                .addListener(new ListenerImpl());

        begin.await(3, TimeUnit.SECONDS);
        end.await(3, TimeUnit.SECONDS);

        assertEquals(0, begin.getCount());
        assertEquals(0, end.getCount());
    }

    public void testControllerLifeCircleWithDelay() throws Exception {
        final int count = 3;
        final CountDownLatch begin = new CountDownLatch(count);
        final CountDownLatch end = new CountDownLatch(count);

        class ListenerImpl implements ControllerListener<BaseResponse> {
            @Override
            public void onBegin() {
                assertTrue(TestUtil.inUIThread());
                begin.countDown();
            }

            @Override
            public void onEnd(final BaseResponse response) {
                assertTrue(TestUtil.inUIThread());
                assertNotNull(response);
                end.countDown();
            }

            @Override
            public void onException(final Throwable throwable) {
                assertNotNull(throwable);
                assertTrue(TestUtil.inUIThread());
            }
        }

        Return<BaseResponse> responseReturn = mController.load();

        Thread.sleep(3000);

        for (int i = 0; i < count; ++i) {
            responseReturn.addListener(new ListenerImpl());
            Thread.sleep(1000);
        }

        begin.await(3, TimeUnit.SECONDS);
        end.await(3, TimeUnit.SECONDS);

        assertEquals(0, begin.getCount());
        assertEquals(0, end.getCount());
    }
}
