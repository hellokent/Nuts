package com.example;

import com.google.common.reflect.Reflection;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.ControllerCallback;
import io.demor.nuts.lib.controller.ControllerInvokeHandler;
import io.demor.nuts.lib.eventbus.EventBarrier;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.nuts.sample.lib.event.TestEvent;

import java.util.concurrent.TimeUnit;

public class MyClass {

    public static void main(String[] args) throws Exception {
        final AppInstance appInstance = new AppInstance("io.demor.nuts.sample", "172.16.141.221");
        EventBarrier<TestEvent> barrier = new EventBarrier<>(appInstance, TestEvent.class);
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler<>(appInstance));
        controller.run(1).asyncUI(new ControllerCallback<String>() {
            @Override
            public void onResult(final String s) {
                controller.sendEvent().sync();
            }
        });
        System.out.println("---------------");
        TestEvent event = barrier.waitForSingleEvent(10, TimeUnit.SECONDS);
        System.out.println("---:" + event);
        barrier.close();
    }
}
