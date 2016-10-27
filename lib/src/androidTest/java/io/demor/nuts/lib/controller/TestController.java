package io.demor.nuts.lib.controller;

import io.demor.nuts.lib.api.BaseResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.demor.nuts.lib.controller.BaseController.of;
import static io.demor.nuts.lib.controller.BaseController.ofVoid;

public interface TestController {

    TestController IMPL = new TestController() {
        @Override
        public Return<BaseResponse> load() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BaseResponse response = new BaseResponse();
            response.msg = "hello";
            return of(response);
        }

        @Override
        public Return<Void> loadBg(CountDownLatch latch) {
            latch.countDown();
            return ofVoid();
        }

        @Override
        public Return<Integer> run(final CountDownLatch latch, int start) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
            return of(start + 1);
        }

        @Override
        public Return<BaseResponse> single(final TestApi2 api, final int count) {
            return of(api.get(count));
        }

        @Override
        public Return<Void> runThrowWrappedException() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            throw new ExceptionWrapper(new IllegalArgumentException());
        }

        @Override
        public Return<Void> runThrowRuntimeException() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            throw new NullPointerException("just test");
        }

        @Override
        public Return<Void> sleep(final int seconds) {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return ofVoid();
        }
    };

    Return<BaseResponse> load();

    Return<Void> loadBg(CountDownLatch latch);

    Return<Integer> run(CountDownLatch latch, int start);

    Return<BaseResponse> single(TestApi2 api, int count);

    Return<Void> runThrowWrappedException();

    Return<Void> runThrowRuntimeException();

    Return<Void> sleep(int seconds);
}
