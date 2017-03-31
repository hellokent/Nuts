package com.example;

import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.eventbus.LogBarrier;

public class MyClass {

    public static void main(String[] args) throws Exception {
        final AppInstance appInstance = new AppInstance("io.demor.nuts.sample", "172.16.141.180");

        new LogBarrier(appInstance).printLog();

//        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(appInstance));
//        controller.add(1).asyncUI(new ControllerCallback<String>() {
//            @Override
//            public void onResult(final String s) {
//                controller.sendEvent().sync();
//            }
//        });
//        try (EventBarrier<TestEvent> barrier = new EventBarrier<>(appInstance, TestEvent.class)){
//            TestEvent event = barrier.waitForSingleEvent(10, TimeUnit.SECONDS);
//            System.out.println(event);
//        }
//        final CountDownLatch latch = new CountDownLatch(1);
//        ListenerBarrier barrier = new ListenerBarrier(appInstance);
//        barrier.registerOnce(new SimpleListener() {
//            @Override
//            public void onGotInt(final int count) {
//                System.out.println("count:" + count);
//                latch.countDown();
//            }
//
//            @Override
//            public void onGotString(final String msg) {
//
//            }
//        }, 5, TimeUnit.SECONDS);
//        controller.callListenerInt(1).asyncUI(null);
//        latch.await();
//        barrier.close();

        Thread.sleep(10000000L);
    }
}
