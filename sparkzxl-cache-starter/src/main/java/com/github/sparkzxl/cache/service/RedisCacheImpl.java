package com.github.sparkzxl.cache.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.github.sparkzxl.cache.redis.CacheHashKey;
import com.github.sparkzxl.cache.redis.CacheKey;
import com.github.sparkzxl.cache.redis.RedisOps;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.lang.NonNull;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * description: redis缓存提供接口实现类
 *
 * @author zhouxinlei
 */
@Slf4j
public class RedisCacheImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisOps redisOps;
    private final ValueOperations<String, Object> valueOperations;

    public RedisCacheImpl(RedisTemplate<String, Object> redisTemplate, RedisOps redisOps) {
        this.redisTemplate = redisTemplate;
        this.redisOps = redisOps;
        valueOperations = redisTemplate.opsForValue();
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
        T obj = Convert.convert(new TypeReference<T>() {
        }, valueOperations.get(key));
        if (obj == null && function != null) {
            obj = function.apply(funcParam);
            Optional.ofNullable(obj).ifPresent(value -> set(key, value, timeout));
        }
        return obj;
    }

    @Override
    public void set(String key, Object value) {
        set(key, value, null);
    }

    @Override
    public void set(String key, Object value, Duration timeout) {
        if (ObjectUtils.isNotEmpty(timeout)) {
            valueOperations.set(key, value, timeout);
        } else {
            valueOperations.set(key, value);
        }
    }

    @Override
    public boolean setIfAbsent(String key, Object value, Duration timeout) {
        if (ObjectUtils.isNotEmpty(timeout)) {
            return Boolean.TRUE.equals(valueOperations.setIfAbsent(key, value, timeout));
        } else {
            return setIfAbsent(key, value);
        }
    }

    @Override
    public boolean setIfAbsent(String key, Object value) {
        return Boolean.TRUE.equals(valueOperations.setIfAbsent(key, value));
    }

    @Override
    public Long increment(String key) {
        return valueOperations.increment(key);
    }

    @Override
    public Long increment(String key, long delta) {
        return valueOperations.increment(key, delta);
    }

    @Override
    public Long decrement(String key) {
        return valueOperations.decrement(key);
    }

    @Override
    public Long decrement(String key, long delta) {
        return valueOperations.decrement(key, delta);
    }

    @Override
    public void remove(String... keys) {
        redisTemplate.delete(Lists.newArrayList(keys));
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> redisConnection.exists(key.getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public void flushDb() {
        redisTemplate.execute((RedisCallback<String>) (connection) -> {
            connection.flushDb();
            return "ok";
        });
    }

    @Override
    public Set<String> keys(@NonNull String pattern) {
        return redisOps.keys(pattern);
    }

    @Override
    public List<String> scan(String pattern) {
        return redisOps.scan(pattern);
    }

    @Override
    public void scanUnlink(String pattern) {
        redisOps.scanUnlink(pattern);
    }

    @Override
    public Boolean expire(@NonNull CacheKey key) {
        assert key.getExpire() != null;
        return redisOps.expire(key.getKey(), key.getExpire());
    }

    @Override
    public Boolean persist(@NonNull CacheKey key) {
        return redisOps.persist(key.getKey());
    }

    @Override
    public String type(@NonNull CacheKey key) {
        return redisOps.type(key.getKey());
    }

    @Override
    public Long ttl(@NonNull CacheKey key) {
        return redisOps.ttl(key.getKey());
    }

    @Override
    public Long pTtl(@NonNull CacheKey key) {
        return redisOps.pTtl(key.getKey());
    }

    @Override
    public void hSet(@NonNull CacheHashKey key, Object value, boolean... cacheNullValues) {
        redisOps.hSet(key, value, cacheNullValues);
    }

    @Override
    public <T> T hGet(@NonNull CacheHashKey key, boolean... cacheNullValues) {
        return redisOps.hGet(key, cacheNullValues);
    }

    @Override
    public <T> T hGet(@NonNull CacheHashKey key, Function<CacheHashKey, T> loader, boolean... cacheNullValues) {
        return redisOps.hGet(key, loader, cacheNullValues);
    }

    @Override
    public Boolean hExists(@NonNull CacheHashKey cacheHashKey) {
        return redisOps.hExists(cacheHashKey);
    }

    @Override
    public Long hDel(@NonNull String key, Object... fields) {
        return redisOps.hDel(key, fields);
    }

    @Override
    public Long hDel(@NonNull CacheHashKey cacheHashKey) {
        return redisOps.hDel(cacheHashKey.getKey(), cacheHashKey.getField());
    }

    @Override
    public Long hLen(@NonNull CacheHashKey key) {
        return redisOps.hLen(key.getKey());
    }

    @Override
    public Long hIncrBy(@NonNull CacheHashKey key, long increment) {
        return redisOps.hIncrBy(key, increment);
    }

    @Override
    public Double hIncrBy(@NonNull CacheHashKey key, double increment) {
        return redisOps.hIncrByFloat(key, increment);
    }

    @Override
    public Set<Object> hKeys(@NonNull CacheHashKey key) {
        return redisOps.hKeys(key.getKey());
    }

    @Override
    public List<Object> hVals(@NonNull CacheHashKey key) {
        return redisOps.hVals(key.getKey());
    }


    @Override
    public <K, V> Map<K, V> hGetAll(@NonNull CacheHashKey key) {
        return redisOps.hGetAll(key);
    }

    @Override
    public <K, V> Map<K, V> hGetAll(@NonNull CacheHashKey key, Function<CacheHashKey, Map<K, V>> loader, boolean... cacheNullValues) {
        return redisOps.hGetAll(key, loader, cacheNullValues);
    }

    @Override
    public Long sAdd(@NonNull CacheKey key, Object value) {
        Long result = redisOps.sAdd(key, value);
        if (key.getExpire() != null) {
            redisOps.expire(key.getKey(), key.getExpire());
        }
        return result;
    }

    @Override
    public Long sRem(@NonNull CacheKey key, Object... members) {
        return redisOps.sRem(key, members);
    }

    @Override
    public Set<Object> sMembers(@NonNull CacheKey key) {
        return redisOps.sMembers(key);
    }

    @Override
    public <T> T sPop(@NonNull CacheKey key) {
        return redisOps.sPop(key);
    }

    @Override
    public Long sCard(@NonNull CacheKey key) {
        return redisOps.sCard(key);
    }
}
