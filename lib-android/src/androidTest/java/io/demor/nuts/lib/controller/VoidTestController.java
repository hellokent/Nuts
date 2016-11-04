package io.demor.nuts.lib.controller;

import java.util.concurrent.CountDownLatch;

import static io.demor.nuts.lib.controller.BaseController.ofVoid;

public interface VoidTestController {

    VoidTestController IMPL = new VoidTestController() {

        @Override
        public Return<Void> load() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return ofVoid();
        }

        @Override
        public Return<Void> loadBg(CountDownLatch latch) {
            latch.countDown();
            return ofVoid();
        }

        @Override
        public Return<Void> run(final CountDownLatch latch, int start) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
            return ofVoid();
        }
    };

    Return<Void> load();

    Return<Void> loadBg(CountDownLatch latch);

    Return<Void> run(CountDownLatch latch, int start);
}
