package io.demor.nuts.lib;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClusterExecutor extends ThreadPoolExecutor {
    private static final String DEFAULT_LOCK = "CLUSTER";

    protected final ThreadLocal<String> cAffineThreadLocal = new ThreadLocal<>();
    protected final ClusterQueue cQueue;

    public ClusterExecutor(final int corePoolSize, final int maximumPoolSize,
                           final long keepAliveTime, final TimeUnit unit, final RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new ClusterQueue(), handler);
        final ClusterThreadFactor factory = new ClusterThreadFactor();
        setThreadFactory(factory);
        cQueue = (ClusterQueue) getQueue();
        cQueue.mAllThreadList = factory.mThreadList;
        cQueue.mAffineThreadLocal = cAffineThreadLocal;
        prestartAllCoreThreads();
    }

    public void execute(final String tag, final Runnable command) {
        cAffineThreadLocal.set(tag);
        try {
            execute(command);
        } finally {
            cAffineThreadLocal.remove();
        }
    }

    protected static class ClusterQueue extends LinkedBlockingQueue<Runnable> {

        public static final Random RANDOM = new Random();

        static {
            RANDOM.setSeed(System.currentTimeMillis());
        }

        public Multimap<String, Runnable> mRunnableMap = MultimapBuilder.hashKeys().linkedListValues().build();
        public Multimap<Thread, String> mAffineThreadMap = MultimapBuilder.hashKeys().linkedListValues().build();
        public Multimap<Thread, String> mAffineLockMap = MultimapBuilder.hashKeys().linkedListValues().build();
        public Set<Thread> mWaitingThreadSet = Sets.newConcurrentHashSet();
        public List<Thread> mAllThreadList;
        public ThreadLocal<String> mAffineThreadLocal;

        @Override
        public synchronized boolean offer(final Runnable runnable) {
            String affine = mAffineThreadLocal.get();
            Thread affineThread = null;
            try {
                if (Strings.isNullOrEmpty(affine)) {
                    affine = DEFAULT_LOCK;
                    if (mWaitingThreadSet.isEmpty()) {
                        affineThread = mAllThreadList.get(RANDOM.nextInt(mAllThreadList.size()));
                    } else {
                        affineThread = mWaitingThreadSet.iterator().next();
                    }
                    return super.offer(runnable);
                } else {
                    if (DEFAULT_LOCK.equals(affine)) {
                        throw new IllegalArgumentException("thread tag cannot be" + DEFAULT_LOCK);
                    }
                    if (mAffineThreadMap.values().contains(affine)) {
                        for (Thread item : mAffineThreadMap.keySet()) {
                            if (mAffineThreadMap.get(item).contains(affine)) {
                                affineThread = item;
                                break;
                            }
                        }
                    } else {
                        Multiset<Thread> threadMultiset = HashMultiset.create();
                        threadMultiset.addAll(mAllThreadList);
                        for (Thread t : mAffineThreadMap.keySet()) {
                            threadMultiset.add(t, mAffineThreadMap.get(t).size());
                        }

                        affineThread = new Ordering<Multiset.Entry<Thread>>() {
                            @Override
                            public int compare(final Multiset.Entry<Thread> left, final Multiset.Entry<Thread> right) {
                                return Ints.compare(left.getCount(), right.getCount());
                            }
                        }.immutableSortedCopy(threadMultiset.entrySet())
                                .get(0).getElement();
                        mAffineThreadMap.put(affineThread, affine);
                    }
                    mRunnableMap.put(affine, runnable);
                    return true;
                }
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            } finally {
                synchronized (this) {
                    mAffineLockMap.put(affineThread, affine);
                }
                Preconditions.checkNotNull(affineThread);
                synchronized (affineThread) {
                    affineThread.notifyAll();
                }
            }
        }

        @Override
        public synchronized int size() {
            return super.size() + mRunnableMap.size();
        }

        @Override
        public synchronized boolean isEmpty() {
            return super.isEmpty() && mRunnableMap.isEmpty();
        }

        @Override
        public synchronized boolean remove(final Object o) {
            if (o == null) {
                return false;
            }
            if (super.remove(o)) {
                return true;
            } else {
                for (String key : mRunnableMap.keySet()) {
                    if (mRunnableMap.get(key).remove(o)) {
                        return true;
                    }
                }
                return false;
            }
        }

        @Override
        public Runnable poll(final long timeout, final TimeUnit unit) throws InterruptedException {
            final Thread t = Thread.currentThread();
            System.out.println("pool:" + t);
            try {
                mWaitingThreadSet.add(t);
                Runnable result = pollRunnableFromMap();
                if (result != null) {
                    return result;
                }
                synchronized (t) {
                    t.wait(unit.toMillis(timeout), 0);
                }
                return pollRunnableFromMap();
            } finally {
                mWaitingThreadSet.remove(t);
            }
        }

        @Override
        public Runnable take() throws InterruptedException {
            final Thread t = Thread.currentThread();
            System.out.println("take:" + t);
            try {
                mWaitingThreadSet.add(t);
                Runnable result = pollRunnableFromMap();
                if (result != null) {
                    return result;
                }
                synchronized (t) {
                    t.wait();
                }
                return pollRunnableFromMap();
            } finally {
                mWaitingThreadSet.remove(t);
            }
        }

        protected synchronized Runnable pollRunnableFromMap() throws InterruptedException {
            final Thread t = Thread.currentThread();
            for (String affine : mAffineLockMap.get(t)) {
                if (DEFAULT_LOCK.equals(affine)) {
                    return super.take();
                }
                Runnable runnable = pollAffineRunnable(affine);
                if (runnable != null) {
                    return runnable;
                }
            }
            return null;
        }

        protected Runnable pollAffineRunnable(String affine) {
            if (mRunnableMap.get(affine).isEmpty()) {
                return null;
            }
            final Iterator<Runnable> i = mRunnableMap.get(affine).iterator();
            final Runnable runnable = i.next();
            if (runnable == null) {
                return null;
            }
            i.remove();
            return runnable;
        }
    }

    private class ClusterThreadFactor implements ThreadFactory {
        private final AtomicInteger mCount = new AtomicInteger(0);
        private ArrayList<Thread> mThreadList = Lists.newArrayList();

        @Override
        public Thread newThread(final Runnable r) {
            Thread t = new Thread(r, "Nuts Task Thread #" + mCount.getAndIncrement());
            mThreadList.add(t);
            return t;
        }
    }


}
