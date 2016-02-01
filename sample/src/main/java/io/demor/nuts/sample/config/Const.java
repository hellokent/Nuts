package io.demor.nuts.sample.config;

import com.google.common.reflect.Reflection;
import com.google.gson.Gson;
import io.demor.nuts.lib.Globals;
import io.demor.nuts.lib.controller.ControllerInvokeHandler;
import io.demor.nuts.lib.jumper.JumperInvokeHandler;
import io.demor.nuts.sample.GlobalApplication;
import io.demor.nuts.sample.controller.TestController;
import io.demor.nuts.sample.controller.impl.TestControllerImpl;

public interface Const extends Globals{
    Gson GSON = new Gson();

    TestController TEST_CONTROLLER = Reflection.newProxy(TestController.class, new ControllerInvokeHandler<>(new TestControllerImpl()));

    Jumper JUMPER = Reflection.newProxy(Jumper.class, new JumperInvokeHandler(GlobalApplication.getGlobalContext()));
}
