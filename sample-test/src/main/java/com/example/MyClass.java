package com.example;

import com.google.common.reflect.Reflection;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.ControllerInvokeHandler;
import io.demor.nuts.sample.lib.controller.TestController;

public class MyClass {

    public static void main(String[] args) {
        AppInstance appInstance = new AppInstance();
        appInstance.mHost = "172.16.141.221";
        appInstance.mPort = 8080;
        TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler<>(appInstance));
        System.out.println(controller.run(1).sync());
        System.out.println(controller.run(2).sync());

    }
}
