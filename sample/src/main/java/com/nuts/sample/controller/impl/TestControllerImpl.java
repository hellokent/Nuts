package com.nuts.sample.controller.impl;

import java.util.concurrent.TimeUnit;

import com.nuts.lib.controller.Return;
import com.nuts.sample.controller.TestController;

public class TestControllerImpl implements TestController {

    static int sCount = 0;

    @Override
    public Return<String> run(final int count) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            sCount += count;
        } catch (InterruptedException e) {

        }
        return new Return<>("Count:" + sCount);
    }
}
