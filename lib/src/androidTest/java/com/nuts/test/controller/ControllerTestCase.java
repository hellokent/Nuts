package com.nuts.test.controller;

import android.os.Looper;
import android.test.AndroidTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.nuts.lib.controller.ControllerCallback;
import com.nuts.lib.controller.ProxyInvokeHandler;
import com.nuts.test.api.BaseResponse;

public class ControllerTestCase extends AndroidTestCase {

    TestController mController = new ProxyInvokeHandler<>(TestController.IMPL).createProxy();

    public void testAsyncRun() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        mController.load()
                .asyncUI(new ControllerCallback<BaseResponse>() {
                    @Override
                    public void onResult(final BaseResponse baseResponse) {
                        assertEquals(Looper.getMainLooper()
                                .getThread(), Thread.currentThread());
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
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; ++i) {
            final int tmp = i;
            mController.run(latch, tmp)
                    .asyncUI(new ControllerCallback<Integer>() {
                        @Override
                        public void onResult(final Integer integer) {
                            assertNotNull(integer);
                            assertEquals(tmp + 1, integer.intValue());
                        }
                    });
        }
        latch.await();
        assertEquals(0, latch.getCount());
    }
}
