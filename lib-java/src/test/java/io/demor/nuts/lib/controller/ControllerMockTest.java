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

    private TestController mController;
    @Before
    public void setUp() throws Exception {
        super.setUp();
        mMockApp.mServer.registerController(TestController.class, TestControllerImpl.IMPL);
        mController = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(mAppInstance));
    }

    @Test
    public void simple() throws Exception {
        int count = mController.get().sync();
        mController.add(10).sync();
        Assert.assertEquals(10 + count, mController.getCount());
    }

    @Test
    public void arrayArgument() throws Exception {
        int count = mController.get().sync();
        mController.addAll(10, 20, 30).sync();
        Assert.assertEquals(60 + count, mController.getCount());
    }

    @Test
    public void waitForSingle() throws Exception {
        final int count = mController.get().sync();
        try (EventBarrier<TestEvent> barrier = new EventBarrier<>(mAppInstance, TestEvent.class)) {
            mController.sendEvent().sync();
            final TestEvent event = barrier.waitForSingleEvent(10, TimeUnit.SECONDS);

            Assert.assertNotNull(event);
            Assert.assertEquals(String.valueOf(count), event.getData());
        }
    }

    @Test
    public void throwException() throws Exception {
        try {
            mController.runWithException().sync();
            Assert.assertTrue(false);
        } catch (ExceptionWrapper wrapper) {
            //wrapper.printStackTrace();
            Assert.assertNotNull(wrapper);
            Assert.assertNotNull(wrapper.getCause());
        }
    }
}
