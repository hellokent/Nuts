package com.nuts.sample.controller;

import java.util.concurrent.TimeUnit;

import com.nuts.lib.controller.CheckActivity;
import com.nuts.lib.controller.Return;
import com.nuts.lib.controller.VoidReturn;
import com.nuts.lib.log.L;

public interface TestController {

    TestController IMPL = new TestController() {

        int mCount;

        @Override
        public Return<String> run(final int count) {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(2));
                mCount += count;
            } catch (InterruptedException e) {
                L.exception(e);
            }
            return new Return<>("Count:" + mCount);
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
    };

    Return<String> run(int count);

    @CheckActivity
    VoidReturn runCheckActivity();
}
