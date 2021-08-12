package com.github.sparkzxl.cache.template;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * description: redis缓存提供接口实现类
 *
 * @author zhouxinlei
 */
@Slf4j
@SuppressWarnings({"ConstantConditions"})
public class RedisCacheImpl implements GeneralCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private ValueOperations<String, Object> valueOperations;

    public RedisCacheImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void initRedisOperation() {
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public <T> T get(String key) {
        return get(key, null, null, null, null);
    }

    @Override
    public <T> T get(String key, Function<String, T> function) {
        return get(key, function, key, null, null);
    }

    @Override
    public <T, M> T get(String key, Function<M, T> function, M funcParam) {
        return get(key, function, funcParam, null, null);
    }

    @Override
    public <T> T get(String key, Function<String, T> function, Long expireTime, TimeUnit timeUnit) {
        return get(key, function, key, expireTime, timeUnit);
    }

    @Override
    public <T, M> T get(String key, Function<M, T> function, M funcParam, Long expireTime, TimeUnit timeUnit) {
        T obj = null;
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        try {
            obj = Convert.convert(new TypeReference<T>() {
            }, valueOperations.get(key));
            if (obj == null && function != null) {
                obj = function.apply(funcParam);
                Optional.ofNullable(obj).ifPresent(value -> set(key, value, expireTime, timeUnit));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return obj;
    }

    @Override
    public void set(String key, Object value) {
        set(key, value, null, null);
    }

    @Override
    public void set(String key, Object value, Long expireTime, TimeUnit timeUnit) {
        if (ObjectUtils.isNotEmpty(expireTime) && ObjectUtils.isNotEmpty(timeUnit)) {
            valueOperations.set(key, value, expireTime, timeUnit);
        } else {
            valueOperations.set(key, value);
        }
    }

    @Override
    public boolean setIfAbsent(String key, Object value, Long expireTime, TimeUnit timeUnit) {
        if (expireTime > 0) {
            return valueOperations.setIfAbsent(key, value, expireTime, timeUnit);
        } else {
            return setIfAbsent(key, value);
        }
    }

    @Override
    public boolean setIfAbsent(String key, Object value) {
        return valueOperations.setIfAbsent(key, value);
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
        return redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> redisConnection.exists(key.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void flushDb() {
        redisTemplate.execute((RedisCallback<String>) (connection) -> {
            connection.flushDb();
            return "ok";
        });
    }

}
