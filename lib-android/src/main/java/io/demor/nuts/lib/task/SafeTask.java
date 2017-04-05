package io.demor.nuts.lib.task;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.demor.nuts.lib.Globals;

public abstract class SafeTask<Param, Result> extends AsyncTask<Param, Void, Result> {

    private static final List<Thread> THREADS = Lists.newArrayList();
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    private static final Object LOCKER = new Object();
    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128), new
            ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "Nuts Task Thread #" + mCount.getAndIncrement());
            THREADS.add(t);
            return t;
        }
    }, new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                executor.setMaximumPoolSize(executor.getMaximumPoolSize() + 16);
                executor.execute(r);
            } else {
                synchronized (LOCKER) {
                    try {
                        LOCKER.wait();
                        executor.execute(r);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }) {
        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            synchronized (LOCKER) {
                LOCKER.notify();
            }
        }
    };

    @SafeVarargs
    public final void safeExecute(final Param... params) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                executeOnExecutor(THREAD_POOL_EXECUTOR, params);
            } else {
                execute(params);
            }
        } else {
            Globals.UI_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    safeExecute(params);
                }
            });
        }
    }
}
