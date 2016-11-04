package io.demor.nuts.lib.eventbus;

import com.google.common.reflect.Reflection;
import io.demor.nuts.lib.BaseTest;
import io.demor.nuts.lib.controller.ControllerInvokeHandler;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.nuts.sample.lib.event.TestEvent;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class EventBusTest extends BaseTest {

    @Test
    public void simple() throws Exception {
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler<>(mAppInstance));
        controller.sendEvent().sync();
        try (EventBarrier<TestEvent> barrier = new EventBarrier<>(mAppInstance, TestEvent.class)) {
            TestEvent event = barrier.waitForSingleEvent(10, TimeUnit.SECONDS);
            System.out.println(event);
        }
    }
}
