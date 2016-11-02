package com.example;

import com.google.common.reflect.Reflection;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.ControllerInvokeHandler;
import io.demor.nuts.lib.eventbus.BaseBarrier;
import io.demor.nuts.lib.module.PushObject;
import io.demor.nuts.sample.lib.controller.TestController;

public class MyClass {

    public static void main(String[] args) throws Exception {
        final AppInstance appInstance = new AppInstance("172.16.141.221", 8080, 40765);
        final Object o = new Object();
        BaseBarrier baseBarrier = new BaseBarrier(appInstance) {
            @Override
            protected void onReceiveData(final PushObject object) {
                synchronized (o) {
                    System.out.println(object.toString());
                    o.notifyAll();
                }

            }
        };
        TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler<>(appInstance));
        controller.sendEvent().sync();
        System.out.println("---------------");

        synchronized (o) {
            o.wait();
        }
    }
}
