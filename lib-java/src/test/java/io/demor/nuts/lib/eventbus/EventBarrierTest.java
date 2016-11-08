package io.demor.nuts.lib.eventbus;

import com.google.common.reflect.Reflection;
import io.demor.nuts.lib.BaseTest;
import io.demor.nuts.lib.controller.ControllerInvokeHandler;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.nuts.sample.lib.event.TestEvent;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventBarrierTest extends BaseTest {

    @Test
    public void waitForSingle() throws Exception {
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(mAppInstance));
        final int count = controller.get().sync();
        try (EventBarrier<TestEvent> barrier = new EventBarrier<>(mAppInstance, TestEvent.class);) {
            controller.sendEvent().sync();
            final TestEvent event = barrier.waitForSingleEvent(10, TimeUnit.SECONDS);

            Assert.assertNotNull(event);
            Assert.assertEquals(String.valueOf(count), event.getData());
        }
    }

    @Test
    public void waitForAll() throws Exception {
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(mAppInstance));
        try (EventBarrier<TestEvent> barrier = new EventBarrier<>(mAppInstance, TestEvent.class)) {
            int count = 10;
            for (int i = 0; i < count; ++i) {
                controller.sendEvent().sync();
            }

            List<TestEvent> events = barrier.waitForAllEvent(100, TimeUnit.MILLISECONDS);
            Assert.assertEquals(count, events.size());
            for (TestEvent e : events) {
                Assert.assertNotNull(e);
            }
        }
    }
}
