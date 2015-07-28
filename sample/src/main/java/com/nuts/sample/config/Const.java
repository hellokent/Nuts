package com.nuts.sample.config;

import com.google.common.reflect.Reflection;
import com.google.gson.Gson;
import com.nuts.lib.Globals;
import com.nuts.lib.controller.ProxyInvokeHandler;
import com.nuts.lib.jumper.JumperInvokeHandler;
import com.nuts.sample.GlobalApplication;
import com.nuts.sample.controller.TestController;
import com.nuts.sample.controller.impl.TestControllerImpl;

public interface Const extends Globals{
    Gson GSON = new Gson();

    TestController TEST_CONTROLLER = new ProxyInvokeHandler<>(new TestControllerImpl()).createProxy();

    Jumper JUMPER = Reflection.newProxy(Jumper.class, new JumperInvokeHandler(GlobalApplication.getGlobalContext()));
}
