package com.nuts.test.controller;

import android.os.Looper;
import android.test.AndroidTestCase;

import java.util.concurrent.CountDownLatch;

import com.nuts.lib.controller.ControllerCallback;
import com.nuts.lib.controller.ProxyInvokeHandler;
import com.nuts.test.api.BaseResponse;

public class ControllerTest extends AndroidTestCase {

    TestController mController = new ProxyInvokeHandler<>(TestController.IMPL).createProxy();

    public void testRun() throws Exception {
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
        latch.await();
        assertEquals(0, latch.getCount());
    }

}
