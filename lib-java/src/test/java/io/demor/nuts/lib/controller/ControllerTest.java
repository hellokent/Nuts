package io.demor.nuts.lib.controller;

import com.google.common.reflect.Reflection;
import io.demor.nuts.lib.BaseTest;
import io.demor.nuts.sample.lib.controller.TestController;
import org.junit.Assert;
import org.junit.Test;

public class ControllerTest extends BaseTest {

    @Test
    public void simple() throws Exception {
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(mAppInstance));
        int count = controller.get().sync();
        Assert.assertEquals("Count:" + (count + 1), controller.add(1).sync());
    }

    @Test
    public void callDirect() throws Exception {
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(mAppInstance));
        int count = controller.get().sync();
        controller.add(10).sync();
        Assert.assertEquals(10 + count, controller.getCount());
    }
}
