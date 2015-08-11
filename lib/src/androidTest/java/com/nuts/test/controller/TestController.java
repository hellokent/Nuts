package com.nuts.test.controller;

import java.util.concurrent.CountDownLatch;

import com.nuts.lib.controller.Return;
import com.nuts.lib.controller.VoidReturn;
import com.nuts.test.api.BaseResponse;

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
            return new Return<>(response);
        }

        @Override
        public VoidReturn loadBg(CountDownLatch latch) {
            latch.countDown();
            return new VoidReturn();
        }

        @Override
        public Return<Integer> run(final CountDownLatch latch, int start) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
            return new Return<>(start + 1);
        }

        @Override
        public Return<BaseResponse> single(final TestApi2 api, final int count) {
            return new Return<>(api.get(count));
        }
    };

    Return<BaseResponse> load();

    VoidReturn loadBg(CountDownLatch latch);

    Return<Integer> run(CountDownLatch latch, int start);

    Return<BaseResponse> single(TestApi2 api, int count);
}
