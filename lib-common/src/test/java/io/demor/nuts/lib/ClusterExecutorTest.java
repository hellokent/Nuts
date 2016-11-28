package io.demor.nuts.lib;

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClusterExecutorTest {

    SubClusterExecutor executor = new SubClusterExecutor(10, 20, 1, TimeUnit.SECONDS, new ThreadPoolExecutor.CallerRunsPolicy());

    @Test
    public void normal() throws Exception {
        int jMax = 21;
        int iMax = 50;
        final CountDownLatch latch = new CountDownLatch(iMax * jMax * 2);
        for (int j = 0; j < jMax; ++j) {
            for (int i = 0; i < iMax; ++i) {
                final String tag = String.valueOf((char) ('a' + j));
                executor.execute(tag, new TagRunnable(tag) {
                    @Override
                    public void run() {
                        super.run();
                        latch.countDown();
                    }
                });
                executor.execute(new TagRunnable(null) {
                    @Override
                    public void run() {
                        super.run();
                        latch.countDown();
                    }
                });
            }
        }
        latch.await(25, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());
    }

    public static class TagRunnable implements Runnable {

        public String tag;

        public TagRunnable(final String tag) {
            this.tag = tag;
        }

        @Override
        public void run() {
            System.out.println("run:" + Thread.currentThread() + ";" + tag);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class SubClusterExecutor extends ClusterExecutor {

        HashMap<String, Thread> map = Maps.newHashMap();

        public SubClusterExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, handler);
        }

        @Override
        protected void beforeExecute(final Thread t, final Runnable r) {
            super.beforeExecute(t, r);
            TagRunnable runnable = (TagRunnable) r;
            if (runnable.tag == null) {
                return;
            }
            if (map.containsKey(runnable.tag)) {
                Assert.assertEquals(map.get(runnable.tag), t);
            } else {
                map.put(runnable.tag, t);
            }
        }
    }
}