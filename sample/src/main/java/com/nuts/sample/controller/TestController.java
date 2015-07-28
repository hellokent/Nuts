package com.nuts.sample.controller;

import java.util.concurrent.TimeUnit;

import com.nuts.lib.annotation.controller.CheckActivity;
import com.nuts.lib.controller.ExceptionWrapper;
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

        @Override
        public VoidReturn runWithException() {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            throw new ExceptionWrapper(new DemoException());
        }
    };

    Return<String> run(int count);

    @CheckActivity
    VoidReturn runCheckActivity();

    VoidReturn runWithException();
}
