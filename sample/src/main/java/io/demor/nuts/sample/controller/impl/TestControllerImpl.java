package io.demor.nuts.sample.controller.impl;

import java.util.concurrent.TimeUnit;

import io.demor.nuts.lib.controller.BaseController;
import io.demor.nuts.lib.controller.ExceptionWrapper;
import io.demor.nuts.lib.controller.Return;
import io.demor.nuts.lib.controller.VoidReturn;
import io.demor.nuts.lib.log.L;
import io.demor.nuts.sample.controller.DemoException;
import io.demor.nuts.sample.controller.TestController;

public class TestControllerImpl extends BaseController implements TestController {

    int mCount;

    @Override
    public Return<String> run(final int count) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            mCount += count;
        } catch (InterruptedException e) {
            L.exception(e);
        }
        return of("Count:" + mCount);
    }

    @Override
    public VoidReturn runCheckActivity() {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ofVoid();
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
}
