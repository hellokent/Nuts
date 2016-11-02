package com.example;

import com.google.common.reflect.Reflection;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.ControllerInvokeHandler;
import io.demor.nuts.lib.eventbus.ListenerBarrier;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.nuts.sample.lib.event.SimpleListener;

import java.util.concurrent.CountDownLatch;

public class MyClass {

    public static void main(String[] args) throws Exception {
        final AppInstance appInstance = new AppInstance("io.demor.nuts.sample", "172.16.141.221");
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler<>(appInstance));
//        controller.run(1).asyncUI(new ControllerCallback<String>() {
//            @Override
//            public void onResult(final String s) {
//                controller.sendEvent().sync();
//            }
//        });
//        try (EventBarrier<TestEvent> barrier = new EventBarrier<>(appInstance, TestEvent.class)){
//            TestEvent event = barrier.waitForSingleEvent(10, TimeUnit.SECONDS);
//            System.out.println(event);
//        }
        final CountDownLatch latch = new CountDownLatch(1);
        ListenerBarrier barrier = new ListenerBarrier(appInstance);
        barrier.register(new SimpleListener() {
            @Override
            public void onGotInt(final int count) {
                System.out.println("count:" + count);
                latch.countDown();
            }

            @Override
            public void onGotString(final String msg) {

            }
        });
        controller.callListenerInt(1).asyncUI(null);
        latch.await();
        barrier.close();
    }
}
