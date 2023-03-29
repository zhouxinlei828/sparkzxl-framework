package com.github.sparkzxl.cache.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.sparkzxl.cache.redis.CacheHashKey;
import com.github.sparkzxl.cache.redis.CacheKey;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * description: Caffeine本地缓存实现
 *
 * @author zhouxinlei
 */
public class CaffeineCacheImpl implements CacheService {

    private final Cache<String, Cache<String, Object>> cacheMap;
    private static final long MAX_SIZE = 1000;

    public CaffeineCacheImpl() {
        this.cacheMap = Caffeine.newBuilder().maximumSize(MAX_SIZE).build();
    }

    @Override
    public void set(String key, Object value) {
        set(key, value, null);
    }

    @Override
    public void set(String key, Object value, Duration timeout) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder().maximumSize(MAX_SIZE);
        if (ObjectUtils.isNotEmpty(timeout)) {
            builder.expireAfterWrite(timeout);
        }
        Cache<String, Object> cache = builder.build();
        cache.put(key, value);
        this.cacheMap.put(key, cache);
    }

    @Override
    public boolean setIfAbsent(String key, Object value, Duration timeout) {
        Cache<String, Object> ifPresent = cacheMap.getIfPresent(key);
        if (ObjectUtils.isEmpty(ifPresent)) {
            set(key, value, timeout);
            return true;
        }
        return false;
    }

    @Override
    public boolean setIfAbsent(String key, Object value) {
        Cache<String, Object> ifPresent = this.cacheMap.getIfPresent(key);
        if (ObjectUtils.isEmpty(ifPresent)) {
            set(key, value);
            return true;
        }
        return false;
    }

    @Override
    public Long increment(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Long obj = get(key, k -> 0L);
        Function<Long, Long> function = x -> {
            LongAdder longAdder = new LongAdder();
            if (ObjectUtils.isNotEmpty(x)) {
                longAdder.add(x);
            }
            longAdder.increment();
            return longAdder.longValue();
        };
        Long result = function.apply(obj);
        set(key, result);
        return result;
    }


    @Override
    public Long increment(String key, long delta) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Long obj = get(key, k -> 0L);
        Function<Long, Long> function = x -> {
            LongAdder longAdder = new LongAdder();
            if (ObjectUtils.isNotEmpty(x)) {
                longAdder.add(x);
            }
            longAdder.add(delta);
            return longAdder.longValue();
        };
        Long result = function.apply(obj);
        set(key, result, null);
        return result;
    }

    @Override
    public Long decrement(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Long obj = get(key, k -> 0L);
        Function<Long, Long> function = x -> {
            LongAdder longAdder = new LongAdder();
            if (ObjectUtils.isNotEmpty(x)) {
                longAdder.add(x);
            }
            longAdder.decrement();
            return longAdder.longValue();
        };
        Long result = function.apply(obj);
        set(key, result, null);
        return result;
    }

    @Override
    public Long decrement(String key, long delta) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Long obj = get(key, k -> 0L);
        Function<Long, Long> function = x -> {
            LongAdder longAdder = new LongAdder();
            longAdder.add(x - delta);
            return longAdder.longValue();
        };
        Long result = function.apply(obj);
        set(key, result, null);
        return result;
    }

    @Override
    public void remove(String... keys) {
        for (String key : keys) {
            this.cacheMap.invalidate(key);
        }
    }

    @Override
    public <T> T get(String key) {
        return get(key, null, null, null);
    }

    @Override
    public <T> T get(String key, Function<String, T> function) {
        return get(key, function, key, null);
    }

    @Override
    public <T, M> T get(String key, Function<M, T> function, M funcParam) {
        return get(key, function, funcParam, null);
    }

    @Override
    public <T> T get(String key, Function<String, T> function, Duration timeout) {
        return get(key, function, key, timeout);
    }

    @Override
    public <T, M> T get(String key, Function<M, T> function, M funcParam, Duration timeout) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Cache<String, Object> cache = cacheMap.get(key, (k) -> {
            Caffeine<Object, Object> builder = Caffeine.newBuilder()
                    .maximumSize(MAX_SIZE);
            if (timeout != null) {
                builder.expireAfterWrite(timeout);
            }
            Cache<String, Object> newCache = builder.build();
            newCache.get(k, (tk) -> {
                if (function != null) {
                    return function.apply(funcParam);
                }
                return null;
            });
            return newCache;
        });
        assert cache != null;
        return Convert.convert(new TypeReference<T>() {
        }, cache.getIfPresent(key), null);
    }


    @Override
    public void flushDb() {
        this.cacheMap.invalidateAll();
    }

    @Override
    public boolean exists(String key) {
        Cache cache = this.cacheMap.getIfPresent(key);
        if (cache == null) {
            return false;
        } else {
            cache.cleanUp();
            return cache.estimatedSize() > 0L;
        }
    }

    @Override
    public Set<String> keys(String pattern) {
        return null;
    }

    @Override
    public List<String> scan(String pattern) {
        return null;
    }

    @Override
    public void scanUnlink(String pattern) {

    }

    @Override
    public Boolean expire(CacheKey key) {
        return null;
    }

    @Override
    public Boolean persist(CacheKey key) {
        return null;
    }

    @Override
    public String type(CacheKey key) {
        return null;
    }

    @Override
    public Long ttl(CacheKey key) {
        return null;
    }

    @Override
    public Long pTtl(CacheKey key) {
        return null;
    }

    @Override
    public void hSet(CacheHashKey key, Object value, boolean... cacheNullValues) {

    }

    @Override
    public <T> T hGet(CacheHashKey key, boolean... cacheNullValues) {
        return null;
    }

    @Override
    public <T> T hGet(CacheHashKey key, Function<CacheHashKey, T> loader, boolean... cacheNullValues) {
        return null;
    }

    @Override
    public Boolean hExists(CacheHashKey cacheHashKey) {
        return null;
    }

    @Override
    public Long hDel(String key, Object... fields) {
        return null;
    }

    @Override
    public Long hDel(CacheHashKey cacheHashKey) {
        return null;
    }

    @Override
    public Long hLen(CacheHashKey key) {
        return null;
    }

    @Override
    public Long hIncrBy(CacheHashKey key, long increment) {
        return null;
    }

    @Override
    public Double hIncrBy(CacheHashKey key, double increment) {
        return null;
    }

    @Override
    public Set<Object> hKeys(CacheHashKey key) {
        return null;
    }

    @Override
    public List<Object> hVals(CacheHashKey key) {
        return null;
    }

    @Override
    public <K, V> Map<K, V> hGetAll(CacheHashKey key) {
        return null;
    }

    @Override
    public <K, V> Map<K, V> hGetAll(CacheHashKey key, Function<CacheHashKey, Map<K, V>> loader, boolean... cacheNullValues) {
        return null;
    }

    @Override
    public Long sAdd(CacheKey key, Object value) {
        return null;
    }

    @Override
    public Long sRem(CacheKey key, Object... members) {
        return null;
    }

    @Override
    public Set<Object> sMembers(CacheKey key) {
        return null;
    }

    @Override
    public <T> T sPop(CacheKey key) {
        return null;
    }

    @Override
    public Long sCard(CacheKey key) {
        return null;
    }
}
