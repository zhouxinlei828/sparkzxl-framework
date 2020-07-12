package com.sparksys.commons.cache.repository;

import java.time.Duration;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sparksys.commons.core.repository.CacheRepository;
import org.springframework.util.StringUtils;


@SuppressWarnings("unchecked")
public class CaffeineRepositoryImpl implements CacheRepository {

    long maxSize = 1000L;
    private final Cache<String, Cache<String, Object>> cacheMap;

    public CaffeineRepositoryImpl() {
        this.cacheMap = Caffeine.newBuilder().maximumSize(this.maxSize).build();
    }

    @Override
    public void set(String key, Object value) {
        set(key, value, null);
    }

    @Override
    public void set(String key, Object value, Long expireTime) {
        Cache<String, Object> cache;
        if (expireTime == null) {
            cache = Caffeine.newBuilder().maximumSize(this.maxSize).build();
        } else {
            cache =
                    Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(expireTime)).maximumSize(this.maxSize).build();
        }
        cache.put(key, value);
        this.cacheMap.put(key, cache);
    }

    @Override
    public Long increment(String key) {
        Function<Long, Long> function = x -> {
            LongAdder longAdder = new LongAdder();
            longAdder.increment();
            return longAdder.longValue();
        };
        return get(key, function, 0L, null);
    }


    @Override
    public Long increment(String key, long delta) {
        Function<Long, Long> function = x -> {
            LongAdder longAdder = new LongAdder();
            longAdder.add(delta);
            return longAdder.longValue();
        };
        return get(key, function, delta, null);
    }

    @Override
    public Long decrement(String key) {
        Function<Long, Long> function = x -> {
            LongAdder longAdder = new LongAdder();
            longAdder.decrement();
            return longAdder.longValue();
        };
        return get(key, function, 0L, null);
    }

    @Override
    public Long decrement(String key, long delta) {
        Function<Long, Long> function = x -> {
            LongAdder longAdder = new LongAdder();
            longAdder.add(-delta);
            return longAdder.longValue();
        };
        return get(key, function, delta, null);
    }

    @Override
    public Long remove(String... keys) {
        for (String key : keys) {
            this.cacheMap.invalidate(key);
        }
        return (long) keys.length;
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
    public <T> T get(String key, Function<String, T> function, Long expireTime) {
        return get(key, function, key, expireTime);
    }

    @Override
    public <T, M> T get(String key, Function<M, T> function, M funcParam, Long expireTime) {
        T obj = null;
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Cache<String, Object> ifPresent = this.cacheMap.getIfPresent(key);
        if (ifPresent == null && function != null) {
            obj = function.apply(funcParam);
            if (obj != null) {
                //设置缓存信息
                set(key, obj, expireTime);
            }
        } else if (ifPresent != null) {
            obj = (T) ifPresent.getIfPresent(key);
        }
        return obj;
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

    public static void main(String[] args) {
        CaffeineRepositoryImpl caffeineRepository = new CaffeineRepositoryImpl();
        Long data = caffeineRepository.increment("hahha", 10);
        System.out.println(data);
        Long datav = caffeineRepository.get("hahha");
        System.out.println(datav);

    }

}