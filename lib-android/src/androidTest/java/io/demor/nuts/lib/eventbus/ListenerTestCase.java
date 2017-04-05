package io.demor.nuts.lib.eventbus;

import android.test.AndroidTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.demor.nuts.lib.Globals;
import io.demor.nuts.lib.TestUtil;
import io.demor.nuts.lib.annotation.eventbus.DeepClone;
import io.demor.nuts.lib.annotation.eventbus.Event;

public class ListenerTestCase extends AndroidTestCase {

    private TestListener mListener;
    private ListenerBus mListenerBus;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mListenerBus = new ListenerBus(Globals.BG_EXECUTOR, Globals.UI_EXECUTOR, null);
        mListener = mListenerBus.provide(TestListener.class);
    }

    public void testSuccess() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);

        final TestListener o = new TestListener() {
            @Override
            @Event(runOn = ThreadType.MAIN)
            public void onSuccess() {
                latch.countDown();
                assertEquals(1, latch.getCount());
                assertTrue(TestUtil.inUIThread());
            }

            @Override
            @Event(runOn = ThreadType.BACKGROUND)
            public void onFailed() {
                latch.countDown();
                assertEquals(0, latch.getCount());
                assertFalse(TestUtil.inUIThread());
            }

            @Override
            public void onDeepClone(int hashCode, Exception e) {

            }

            @Override
            public void onDirect(int hashCode, Exception e) {
            }
        };

        mListenerBus.register(TestListener.class, o);

        new Thread() {
            @Override
            public void run() {
                mListener.onSuccess();
                try {
                    sleep(500);
                } catch (InterruptedException ignored) {
                }
                mListener.onFailed();
            }
        }.start();

        latch.await(2, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());

    }

    public void testDeepCloneMethod() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);

        final TestListener o = new TestListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailed() {
            }

            @Override
            public void onDeepClone(int hashCode, Exception e) {
                latch.countDown();
                assertNotSame(hashCode, e.hashCode());
            }

            @Override
            public void onDirect(int hashCode, Exception e) {
                latch.countDown();
                assertEquals(hashCode, e.hashCode());
            }
        };

        mListenerBus.register(TestListener.class, o);

        new Thread() {
            @Override
            public void run() {
                {
                    Exception e = new Exception();
                    mListener.onDeepClone(e.hashCode(), e);
                }
                {
                    Exception e = new Exception();
                    mListener.onDirect(e.hashCode(), e);
                }
            }
        }.start();

        latch.await(2, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
    }

    public void testIllegal() throws Exception {
        try {
            IllegalInterface i = mListenerBus.provide(IllegalInterface.class);
            assertTrue(false);
        } catch (Throwable t) {
        }

        try {
            mListenerBus.register(IllegalInterface.class, new IllegalInterface() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailed() {

                }

                @Override
                public void onDeepClone(int hashCode, Exception e) {

                }

                @Override
                public void onDirect(int hashCode, Exception e) {

                }
            });
            assertTrue(false);
        } catch (Throwable ignored) {
        }
    }

    public void testDeepCloneClass() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);

        final IDeepClone o = new IDeepClone() {
            @Override
            public void onDeepClone(int hashCode, Exception e) {
                latch.countDown();
                assertNotSame(hashCode, e.hashCode());
            }

            @Override
            public void onDirect(int hashCode, Exception e) {
                latch.countDown();
                assertNotSame(hashCode, e.hashCode());
            }
        };

        mListenerBus.register(IDeepClone.class, o);

        final IDeepClone provider = mListenerBus.provide(IDeepClone.class);
        assertTrue(provider == mListenerBus.provide(IDeepClone.class));

        new Thread() {
            @Override
            public void run() {
                {
                    Exception e = new Exception();
                    provider.onDeepClone(e.hashCode(), e);
                }
                {
                    Exception e = new Exception();
                    provider.onDirect(e.hashCode(), e);
                }
            }
        }.start();

        latch.await(2, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
    }

    @Override
    public void tearDown() throws Exception {
        mListenerBus.clear();
    }

    public void testTwoInterface() throws Exception {
        final CountDownLatch foo = new CountDownLatch(1);
        final CountDownLatch bar = new CountDownLatch(1);

        FooBar fooBar = new FooBar() {
            @Override
            public void bar() {
                bar.countDown();
            }

            @Override
            public void foo() {
                foo.countDown();
            }
        };

        mListenerBus.register(IFoo.class, fooBar);
        mListenerBus.register(IBar.class, fooBar);
        final IFoo fooProvider = mListenerBus.provide(IFoo.class);
        final IBar barProvider = mListenerBus.provide(IBar.class);

        new Thread() {
            @Override
            public void run() {
                fooProvider.foo();
                barProvider.bar();
            }
        }.start();

        foo.await(1, TimeUnit.SECONDS);
        bar.await(1, TimeUnit.SECONDS);
        assertEquals(0, foo.getCount());
        assertEquals(0, bar.getCount());
    }

    private interface IllegalInterface extends TestListener {
    }

    @DeepClone
    private interface IDeepClone {
        @DeepClone
        void onDeepClone(int hashCode, Exception e);

        void onDirect(int hashCode, Exception e);
    }

    private interface IFoo {
        void foo();
    }

    private interface IBar {
        void bar();
    }

    private abstract class FooBar implements IFoo, IBar {
    }
}
