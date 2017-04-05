package io.demor.nuts.lib.eventbus;

import com.google.common.reflect.Reflection;
import io.demor.nuts.lib.BasePhoneTest;
import io.demor.nuts.lib.controller.ControllerInvokeHandler;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.nuts.sample.lib.event.SimpleListener;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ListenerBarrierPhoneTest extends BasePhoneTest {

    @Test
    public void simple() throws Exception {
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(mAppInstance));
        final CountDownLatch latch = new CountDownLatch(2);
        try (ListenerBarrier barrier = new ListenerBarrier(mAppInstance)) {
            controller.callListenerInt(1).sync();
            controller.callListenerString("msg").sync();
            barrier.registerDuring(new SimpleListener() {
                @Override
                public void onGotInt(final int count) {
                    Assert.assertEquals(1, count);
                    latch.countDown();
                }

                @Override
                public void onGotString(final String msg) {
                    Assert.assertEquals("msg", msg);
                    latch.countDown();
        }
            }, 5, TimeUnit.SECONDS);
        }
        latch.await(5, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());
    }
}
