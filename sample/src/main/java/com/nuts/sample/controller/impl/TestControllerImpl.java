package com.nuts.sample.controller.impl;

import java.util.concurrent.TimeUnit;

import com.nuts.lib.controller.Return;
import com.nuts.lib.controller.VoidReturn;
import com.nuts.lib.log.L;
import com.nuts.sample.controller.TestController;

public class TestControllerImpl implements TestController {

    static int sCount = 0;

    @Override
    public Return<String> run(final int count) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            sCount += count;
        } catch (InterruptedException e) {
            L.exception(e);
        }
        return new Return<>("Count:" + sCount);
    }

    @Override
    public VoidReturn runCheckActivity() {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new VoidReturn();
    }
}
