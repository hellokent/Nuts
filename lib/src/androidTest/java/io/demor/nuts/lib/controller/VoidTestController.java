package io.demor.nuts.lib.controller;

import java.util.concurrent.CountDownLatch;

public interface VoidTestController {

    VoidTestController IMPL = new VoidTestController() {

        @Override
        public VoidReturn load() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new VoidReturn();
        }

        @Override
        public VoidReturn loadBg(CountDownLatch latch) {
            latch.countDown();
            return new VoidReturn();
        }

        @Override
        public VoidReturn run(final CountDownLatch latch, int start) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
            return new VoidReturn();
        }
    };

    VoidReturn load();

    VoidReturn loadBg(CountDownLatch latch);

    VoidReturn run(CountDownLatch latch, int start);
}
