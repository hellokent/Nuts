package com.nuts.test.controller;

import com.nuts.lib.controller.Return;
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
            return new Return<BaseResponse>(new BaseResponse());
        }
    };

    Return<BaseResponse> load();
}
