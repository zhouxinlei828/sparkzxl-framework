package com.github.sparkzxl.signature.server.cache;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SignLocalCache implements SignCache, Serializable {

    private static final Map<String, CacheObj> LOCAL_CACHE = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock(true);
    private final Lock writeLock = cacheLock.writeLock();
    private final Lock readLock = cacheLock.readLock();

    /**
     * The cache expiration time is 1 day by default
     */
    public static long timeout = TimeUnit.DAYS.toMillis(1);

    /**
     * Turn on the timed task of clearing the local memory cache.
     * After it is turned on, the {@link SignLocalCache#pruneCache()} method will be called to automatically clear the cache.
     * If you customize the implemented jap cache interface, you can ignore this config.
     */
    public static boolean schedulePrune = true;

    public SignLocalCache() {
        if (schedulePrune) {
            this.schedulePrune(timeout);
        }
    }

    @Override
    public void set(String key, Serializable value) {
        set(key, value, -1);
    }

    @Override
    public void set(String key, Serializable value, long timeout) {
        writeLock.lock();
        try {
            LOCAL_CACHE.put(key, new CacheObj(value, timeout));
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Serializable get(String key) {
        if (StrUtil.isEmpty(key)) {
            return null;
        }
        readLock.lock();
        try {
            CacheObj cacheObj = LOCAL_CACHE.get(key);
            if (null == cacheObj || cacheObj.isExpired()) {
                return null;
            }
            return cacheObj.getData();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsKey(String key) {
        if (StrUtil.isEmpty(key)) {
            return false;
        }
        readLock.lock();
        try {
            CacheObj cacheObj = LOCAL_CACHE.get(key);
            return null != cacheObj && !cacheObj.isExpired();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void removeKey(String key) {
        writeLock.lock();
        try {
            LOCAL_CACHE.remove(key);
        } finally {
            writeLock.unlock();
        }
    }


    /**
     * Start a scheduled task to clean up expired cache
     *
     * @param delay Interval duration, in milliseconds
     */
    public void schedulePrune(long delay) {
        AuthCacheScheduler.INSTANCE.schedule(this::pruneCache, delay);
    }

    /**
     * Clean up expired cache
     */
    public void pruneCache() {
        Iterator<CacheObj> values = LOCAL_CACHE.values().iterator();
        CacheObj cacheObj;
        while (values.hasNext()) {
            cacheObj = values.next();
            if (cacheObj.isExpired()) {
                values.remove();
            }
        }
    }

    /**
     * Cache scheduler
     */
    private enum AuthCacheScheduler {
        /**
         * AuthCacheScheduler
         */
        INSTANCE;

        private final AtomicInteger cacheTaskNumber = new AtomicInteger(1);
        private ScheduledExecutorService scheduler;

        AuthCacheScheduler() {
            create();
        }

        private void create() {
            this.shutdown();
            this.scheduler = new ScheduledThreadPoolExecutor(10, r -> new Thread(r, String.format("signature-task-%s", cacheTaskNumber.getAndIncrement())));
        }

        public void shutdown() {
            if (null != scheduler) {
                this.scheduler.shutdown();
            }
        }

        public void schedule(Runnable task, long delay) {
            this.scheduler.scheduleAtFixedRate(task, delay, delay, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * description: Cache Object
     *
     * @author zhouxinlei
     * @since 2024-05-21 14:26:10
     */
    @Getter
    private static class CacheObj implements Serializable {
        private final Serializable data;
        private final long expire;

        CacheObj(Serializable data, long expire) {
            this.data = data;
            // The actual expiration time is equal to the current time plus the validity period
            this.expire = System.currentTimeMillis() + expire;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > this.expire;
        }

    }
}
