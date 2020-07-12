package com.sparksys.commons.cache.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.sparksys.commons.core.repository.CacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * description：Guava Cache
 *
 * @author zhouxinlei
 * @date 2020/6/17 0017
 */
@Slf4j
public class GuavaCacheRepositoryImpl implements CacheRepository {

    private static final Map<String, Cache<String, Object>> CACHE_CONCURRENT_MAP = Maps.newConcurrentMap();

    private static final long CACHE_MAXIMUM_SIZE = 100;

    private static final long CACHE_MINUTE = 10 * 60;

    private static final Lock LOCK = new ReentrantLock();

    static {
        Cache<String, Object> cacheContainer = CacheBuilder.newBuilder()
                .maximumSize(CACHE_MAXIMUM_SIZE)
                //最后一次写入后的一段时间移出
                .expireAfterWrite(CACHE_MINUTE, TimeUnit.SECONDS)
                //.expireAfterAccess(CACHE_MINUTE, TimeUnit.MILLISECONDS) //最后一次访问后的一段时间移出
                .recordStats()//开启统计功能
                .build();
        CACHE_CONCURRENT_MAP.put(String.valueOf(CACHE_MINUTE), cacheContainer);
    }

    /**
     * 查询缓存
     *
     * @param key 缓存键 不可为空
     **/
    @Override
    public <T> T get(String key) {
        return get(key, null, null, CACHE_MINUTE);
    }

    /**
     * 查询缓存
     *
     * @param key      缓存键 不可为空
     * @param function 如没有缓存，调用该callable函数返回对象 可为空
     **/
    @Override
    public <T> T get(String key, Function<String, T> function) {
        return get(key, function, key, CACHE_MINUTE);
    }

    /**
     * 查询缓存
     *
     * @param key       缓存键 不可为空
     * @param function  如没有缓存，调用该callable函数返回对象 可为空
     * @param funcParam function函数的调用参数
     **/
    @Override
    public <T, M> T get(String key, Function<M, T> function, M funcParam) {
        return get(key, function, funcParam, CACHE_MINUTE);
    }

    /**
     * 查询缓存
     *
     * @param key        缓存键 不可为空
     * @param function   如没有缓存，调用该callable函数返回对象 可为空
     * @param expireTime 过期时间（单位：毫秒） 可为空
     **/
    @Override
    public <T> T get(String key, Function<String, T> function, Long expireTime) {
        return get(key, function, key, expireTime);
    }

    /**
     * 查询缓存
     *
     * @param key        缓存键 不可为空
     * @param function   如没有缓存，调用该callable函数返回对象 可为空
     * @param funcParam  function函数的调用参数
     * @param expireTime 过期时间（单位：毫秒） 可为空
     **/
    @Override
    public <T, M> T get(String key, Function<M, T> function, M funcParam, Long expireTime) {
        T obj = null;
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        expireTime = getExpireTime(expireTime);
        Cache<String, Object> cacheContainer = getCacheContainer(expireTime);
        try {
            if (function == null) {
                obj = (T) cacheContainer.getIfPresent(key);
            } else {
                obj = (T) cacheContainer.get(key, () -> function.apply(funcParam));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return obj;
    }

    /**
     * 设置缓存键值  直接向缓存中插入值，这会直接覆盖掉给定键之前映射的值
     *
     * @param key 缓存键 不可为空
     * @param obj 缓存值 不可为空
     **/
    @Override
    public void set(String key, Object obj) {
        set(key, obj, CACHE_MINUTE);
    }

    /**
     * 设置缓存键值  直接向缓存中插入值，这会直接覆盖掉给定键之前映射的值
     *
     * @param key        缓存键 不可为空
     * @param obj        缓存值 不可为空
     * @param expireTime 过期时间（单位：毫秒） 可为空
     **/
    @Override
    public void set(String key, Object obj, Long expireTime) {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        if (obj == null) {
            return;
        }
        expireTime = getExpireTime(expireTime);
        Cache<String, Object> cacheContainer = getCacheContainer(expireTime);
        cacheContainer.put(key, obj);
    }

    @Override
    public Long increment(String key) {
        Long expireTime = getExpireTime(CACHE_MINUTE);
        Cache<String, Object> cacheContainer = getCacheContainer(expireTime);
        Supplier<LongAdder> function = LongAdder::new;
        LongAdder longAdder;
        try {
            longAdder = (LongAdder) cacheContainer.get(key, function::get);
            longAdder.increment();
            cacheContainer.put(key, longAdder);
            return longAdder.longValue();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public Long increment(String key, long delta) {
        Long expireTime = getExpireTime(CACHE_MINUTE);
        Cache<String, Object> cacheContainer = getCacheContainer(expireTime);
        Supplier<LongAdder> function = LongAdder::new;
        LongAdder longAdder;
        try {
            longAdder = (LongAdder) cacheContainer.get(key, function::get);
            longAdder.add(delta);
            cacheContainer.put(key, longAdder);
            return longAdder.longValue();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public Long decrement(String key, long delta) {
        Long expireTime = getExpireTime(CACHE_MINUTE);
        Cache<String, Object> cacheContainer = getCacheContainer(expireTime);
        Supplier<LongAdder> function = LongAdder::new;
        LongAdder longAdder;
        try {
            longAdder = (LongAdder) cacheContainer.get(key, function::get);
            longAdder.add(-delta);
            cacheContainer.put(key, longAdder);
            return longAdder.longValue();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public Long decrement(String key) {
        Long expireTime = getExpireTime(CACHE_MINUTE);
        Cache<String, Object> cacheContainer = getCacheContainer(expireTime);
        Supplier<LongAdder> function = LongAdder::new;
        LongAdder longAdder;
        try {
            longAdder = (LongAdder) cacheContainer.get(key, function::get);
            longAdder.decrement();
            cacheContainer.put(key, longAdder);
            return longAdder.longValue();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public Long remove(String... keys) {
        Iterable iterable = Arrays.asList(keys);
        if (Iterables.isEmpty(iterable)) {
            return 0L;
        }
        long expireTime = getExpireTime(CACHE_MINUTE);
        Cache<String, Object> cacheContainer = getCacheContainer(expireTime);
        cacheContainer.invalidateAll(iterable);
        return (long) keys.length;
    }

    @Override
    public boolean exists(String key) {
        boolean exists = false;
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        Object obj = get(key);
        if (obj != null) {
            exists = true;
        }
        return exists;
    }

    @Override
    public void flushDb() {
        long expireTime = getExpireTime(CACHE_MINUTE);
        Cache<String, Object> cacheContainer = getCacheContainer(expireTime);
        cacheContainer.invalidateAll();
    }

    private Cache<String, Object> getCacheContainer(Long expireTime) {
        Cache<String, Object> cacheContainer;
        if (expireTime == null) {
            return null;
        }
        String mapKey = String.valueOf(expireTime);
        if (CACHE_CONCURRENT_MAP.containsKey(mapKey)) {
            cacheContainer = CACHE_CONCURRENT_MAP.get(mapKey);
            return cacheContainer;
        }
        LOCK.lock();
        try {
            cacheContainer = CacheBuilder.newBuilder()
                    .maximumSize(CACHE_MAXIMUM_SIZE)
                    //最后一次写入后的一段时间移出
                    .expireAfterWrite(expireTime, TimeUnit.SECONDS)
                    //.expireAfterAccess(AppConst.CACHE_MINUTE, TimeUnit.MILLISECONDS) //最后一次访问后的一段时间移出
                    .recordStats()//开启统计功能
                    .build();
            CACHE_CONCURRENT_MAP.put(mapKey, cacheContainer);
        } finally {
            LOCK.unlock();
        }
        return cacheContainer;
    }

    /**
     * 获取过期时间 单位：秒
     *
     * @param expireTime 传人的过期时间 单位秒 如小于1分钟，默认为10分钟
     **/
    private Long getExpireTime(Long expireTime) {
        Long result = expireTime;
        if (expireTime == null || expireTime < CACHE_MINUTE / 10) {
            result = CACHE_MINUTE;
        }
        return result;
    }
}
