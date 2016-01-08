package io.demor.nuts.lib.controller;

import android.test.AndroidTestCase;
import com.google.common.reflect.Reflection;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;

public class RxControllerTest extends AndroidTestCase {

    TestController mController = Reflection.newProxy(TestController.class, new RxControllerHandler<>(TestController.IMPL));

    public void testRun() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        mController.run(2, 10)
                .observeOn(Schedulers.computation())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        latch.countDown();
                        assertEquals(11, integer.intValue());
                    }
                });
        latch.await();
    }
}
