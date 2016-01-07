package io.demor.nuts.lib.controller;

import rx.Observable;

import java.util.concurrent.TimeUnit;

public interface TestController {

    TestController IMPL = new TestController() {
        @Override
        public Observable<Integer> run(int sleepSeconds, int code) {
            try {
                Thread.currentThread().sleep(TimeUnit.SECONDS.toMillis(sleepSeconds));
            } catch (InterruptedException e) {
            }
            return Observable.just(code + 1);
        }
    };

    Observable<Integer> run(int sleepSeconds, int code);
}
