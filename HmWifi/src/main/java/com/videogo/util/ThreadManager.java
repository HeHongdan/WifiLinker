package com.videogo.util;

import android.os.Build;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes5.dex */
public class ThreadManager {
    public static int CPU_NUMS = Math.max(1, Runtime.getRuntime().availableProcessors());
    
    public static final String DEFAULT_SINGLE_POOL_NAME = "DEFAULT_SINGLE_POOL_NAME";
    public static final long KEEP_ALIVE = 5;
    public static volatile ThreadPoolProxy mDownloadPool;
    public static volatile ThreadPoolProxy mLongPool;
    public static volatile ThreadPoolProxy mPlayPool;
    public static volatile ThreadPoolProxy mPrePlayPool;
    public static volatile ThreadPoolProxy mShortPool;
    public static Object mLongLock = new Object();
    public static Object mShortLock = new Object();
    public static Object mDownloadLock = new Object();
    public static Object mPlayLock = new Object();
    public static Object mPrePlayLock = new Object();
    public static Map<String, ThreadPoolProxy> mMap = new ConcurrentHashMap();
    public static Object mSingleLock = new Object();

    /* loaded from: classes5.dex */
    public static class ThreadPoolProxy {
        public int mCorePoolSize;
        public long mKeepAliveTime;
        public int mMaximumPoolSize;
        public String mNamePrefix;
        public ThreadPoolExecutor mPool;
        public int mPriority;

        /* loaded from: classes5.dex */
        public static class DefaultThreadFactory implements ThreadFactory {
            public static final AtomicInteger poolNumber = new AtomicInteger(1);
            public final ThreadGroup group;
            public final String namePrefix;
            public int priority;
            public final AtomicInteger threadNumber = new AtomicInteger(1);

            public DefaultThreadFactory(String str, int i) {
                ThreadGroup threadGroup;
                SecurityManager securityManager = System.getSecurityManager();
                if (securityManager != null) {
                    threadGroup = securityManager.getThreadGroup();
                } else {
                    threadGroup = Thread.currentThread().getThreadGroup();
                }
                this.group = threadGroup;
                this.namePrefix = str + "-pool-" + poolNumber.getAndIncrement() + "-thread-";
                if (i == 0) {
                    this.priority = 5;
                } else {
                    this.priority = i;
                }
            }

            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable runnable) {
                ThreadGroup threadGroup = this.group;
                Thread thread = new Thread(threadGroup, runnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
                if (thread.isDaemon()) {
                    thread.setDaemon(false);
                }
                int priority = thread.getPriority();
                int i = this.priority;
                if (priority != i) {
                    thread.setPriority(i);
                }
                return thread;
            }
        }

        public synchronized void cancel(Runnable runnable) {
            if ((this.mPool != null && !this.mPool.isShutdown()) || this.mPool.isTerminating()) {
                this.mPool.getQueue().remove(runnable);
            }
        }

        public synchronized void cancelAllTask() {
            if ((this.mPool != null && !this.mPool.isShutdown()) || this.mPool.isTerminating()) {
                this.mPool.getQueue().clear();
            }
        }

        public synchronized boolean contains(Runnable runnable) {
            if (this.mPool == null || (this.mPool.isShutdown() && !this.mPool.isTerminating())) {
                return false;
            }
            return this.mPool.getQueue().contains(runnable);
        }

        public synchronized void execute(Runnable runnable) {
            if (runnable == null) {
                return;
            }
            if (this.mPool == null || this.mPool.isShutdown()) {
                this.mPool = getThreadPool();
            }
            try {
                this.mPool.execute(runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public ThreadPoolExecutor getThreadPool() {
            ThreadPoolExecutor threadPoolExecutor = this.mPool;
            if (threadPoolExecutor == null || threadPoolExecutor.isShutdown()) {
                this.mPool = new ThreadPoolExecutor(this.mCorePoolSize, this.mMaximumPoolSize, this.mKeepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), new DefaultThreadFactory(this.mNamePrefix, this.mPriority), new ThreadPoolExecutor.AbortPolicy());
            }
            return this.mPool;
        }

        public synchronized void shutdown() {
            if (this.mPool != null && (!this.mPool.isShutdown() || this.mPool.isTerminating())) {
                this.mPool.shutdownNow();
            }
        }

        public void stop() {
            ThreadPoolExecutor threadPoolExecutor = this.mPool;
            if (threadPoolExecutor != null) {
                if (!threadPoolExecutor.isShutdown() || this.mPool.isTerminating()) {
                    this.mPool.shutdown();
                }
            }
        }

        public synchronized Future<?> submit(Runnable runnable) {
            if (runnable == null) {
                return null;
            }
            if (this.mPool == null || this.mPool.isShutdown()) {
                this.mPool = getThreadPool();
            }
            try {
                return this.mPool.submit(runnable);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public ThreadPoolProxy(int i, int i2, long j, String str) {
            this.mCorePoolSize = i;
            this.mMaximumPoolSize = i2;
            this.mKeepAliveTime = j;
            this.mNamePrefix = str;
        }

        public ThreadPoolProxy(int i, int i2, long j, String str, int i3) {
            this.mCorePoolSize = i;
            this.mMaximumPoolSize = i2;
            this.mKeepAliveTime = j;
            this.mNamePrefix = str;
            this.mPriority = i3;
        }
    }

    public static ThreadPoolProxy getDownloadPool() {
        ThreadPoolProxy threadPoolProxy;
        if (mDownloadPool != null) {
            return mDownloadPool;
        }
        synchronized (mDownloadLock) {
            if (mDownloadPool == null) {
                int max = Math.max(5, (CPU_NUMS * 2) + 1);
                mDownloadPool = new ThreadPoolProxy(max, max, 5L, "Download");
            }
            threadPoolProxy = mDownloadPool;
        }
        return threadPoolProxy;
    }

    public static ThreadPoolProxy getLongPool() {
        ThreadPoolProxy threadPoolProxy;
        if (mLongPool != null) {
            return mLongPool;
        }
        synchronized (mLongLock) {
            if (mLongPool == null) {
                int max = Math.max(5, (CPU_NUMS * 2) + 1);
                mLongPool = new ThreadPoolProxy(max, max, 5L, "Long");
            }
            threadPoolProxy = mLongPool;
        }
        return threadPoolProxy;
    }

    public static ThreadPoolProxy getPlayPool() {
        ThreadPoolProxy threadPoolProxy;
        if (mPlayPool != null) {
            return mPlayPool;
        }
        synchronized (mPlayLock) {
            if (mPlayPool == null) {
                if (Build.VERSION.SDK_INT >= 21 && CPU_NUMS > 2) {
                    if (Build.VERSION.SDK_INT < 23 && CPU_NUMS <= 4) {
                        mPlayPool = new ThreadPoolProxy(8, 8, 5L, "Play");
                    } else if (Build.VERSION.SDK_INT < 24) {
                        mPlayPool = new ThreadPoolProxy(16, 16, 5L, "Play");
                    } else {
                        mPlayPool = new ThreadPoolProxy(32, 32, 5L, "Play");
                    }
                }
                mPlayPool = new ThreadPoolProxy(4, 4, 5L, "Play");
            }
            threadPoolProxy = mPlayPool;
        }
        return threadPoolProxy;
    }

    public static ThreadPoolProxy getPrePlayPool() {
        ThreadPoolProxy threadPoolProxy;
        if (mPrePlayPool != null) {
            return mPrePlayPool;
        }
        synchronized (mPrePlayLock) {
            if (mPrePlayPool == null) {
                if (Build.VERSION.SDK_INT >= 21 && CPU_NUMS > 2) {
                    mPrePlayPool = new ThreadPoolProxy(4, 4, 5L, "PrePlay");
                }
                mPrePlayPool = new ThreadPoolProxy(2, 2, 5L, "PrePlay");
            }
            threadPoolProxy = mPrePlayPool;
        }
        return threadPoolProxy;
    }

    public static ThreadPoolProxy getShortPool() {
        ThreadPoolProxy threadPoolProxy;
        if (mShortPool != null) {
            return mShortPool;
        }
        synchronized (mShortLock) {
            if (mShortPool == null) {
                mShortPool = new ThreadPoolProxy(2, 2, 5L, "Short");
            }
            threadPoolProxy = mShortPool;
        }
        return threadPoolProxy;
    }

    public static ThreadPoolProxy getSinglePool() {
        return getSinglePool(DEFAULT_SINGLE_POOL_NAME);
    }

    public static void shutdownAll() {
        if (mPlayPool != null) {
            mPlayPool.shutdown();
        }
        if (mPrePlayPool != null) {
            mPrePlayPool.shutdown();
        }
        if (mLongPool != null) {
            mLongPool.shutdown();
        }
        if (mShortPool != null) {
            mShortPool.shutdown();
        }
        if (mDownloadPool != null) {
            mDownloadPool.shutdown();
        }
    }

    public static void stopAll() {
        if (mPlayPool != null) {
            mPlayPool.stop();
        }
        if (mPrePlayPool != null) {
            mPrePlayPool.stop();
        }
        if (mLongPool != null) {
            mLongPool.stop();
        }
        if (mShortPool != null) {
            mShortPool.stop();
        }
        if (mDownloadPool != null) {
            mDownloadPool.stop();
        }
    }

    public static ThreadPoolProxy getSinglePool(String str) {
        ThreadPoolProxy threadPoolProxy;
        ThreadPoolProxy threadPoolProxy2 = mMap.get(str);
        if (threadPoolProxy2 != null) {
            return threadPoolProxy2;
        }
        synchronized (mSingleLock) {
            threadPoolProxy = mMap.get(str);
            if (threadPoolProxy == null) {
                threadPoolProxy = new ThreadPoolProxy(1, 1, 5L, str);
                mMap.put(str, threadPoolProxy);
            }
        }
        return threadPoolProxy;
    }
}