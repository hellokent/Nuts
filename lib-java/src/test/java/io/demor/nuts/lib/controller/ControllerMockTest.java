package io.demor.nuts.lib.controller;

import com.google.common.reflect.Reflection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import io.demor.nuts.lib.BaseMockTest;
import io.demor.nuts.lib.TestControllerImpl;
import io.demor.nuts.lib.eventbus.EventBarrier;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.nuts.sample.lib.event.TestEvent;

public class ControllerMockTest extends BaseMockTest{

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mMockApp.mServer.registerController(TestController.class, TestControllerImpl.IMPL);
    }

    @Test
    public void simple() throws Exception {
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(mAppInstance));
        int count = controller.get().sync();
        controller.add(10).sync();
        Assert.assertEquals(10 + count, controller.getCount());
    }

    @Test
    public void arrayArgument() throws Exception {
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(mAppInstance));
        int count = controller.get().sync();
        controller.addAll(10, 20, 30).sync();
        Assert.assertEquals(60 + count, controller.getCount());
    }

    @Test
    public void waitForSingle() throws Exception {
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(mAppInstance));
        final int count = controller.get().sync();
        try (EventBarrier<TestEvent> barrier = new EventBarrier<>(mAppInstance, TestEvent.class)) {
            controller.sendEvent().sync();
            final TestEvent event = barrier.waitForSingleEvent(10, TimeUnit.SECONDS);

            Assert.assertNotNull(event);
            Assert.assertEquals(String.valueOf(count), event.getData());
        }
    }
}
