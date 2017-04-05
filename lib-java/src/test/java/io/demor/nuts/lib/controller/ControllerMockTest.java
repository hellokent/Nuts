package io.demor.nuts.lib.controller;

import com.google.common.reflect.Reflection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.demor.nuts.lib.BaseMockTest;
import io.demor.nuts.lib.TestControllerImpl;
import io.demor.nuts.sample.lib.controller.TestController;

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
}
