package io.demor.nuts.lib.eventbus;

import com.google.common.reflect.Reflection;
import io.demor.nuts.lib.BaseTest;
import io.demor.nuts.lib.controller.ControllerInvokeHandler;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.nuts.sample.lib.event.SimpleListener;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ListenerBus extends BaseTest {

    @Test
    public void simple() throws Exception {
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler<>(mAppInstance));
        final CountDownLatch latch = new CountDownLatch(1);
        ListenerBarrier barrier = new ListenerBarrier(mAppInstance);
        barrier.registerOnce(new SimpleListener() {
            @Override
            public void onGotInt(final int count) {
                System.out.println("count:" + count);
                latch.countDown();
            }

            @Override
            public void onGotString(final String msg) {

            }
        }, 5, TimeUnit.SECONDS);
        controller.callListenerInt(1).asyncUI(null);
        latch.await();
        barrier.close();

    }
}
