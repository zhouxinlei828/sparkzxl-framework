package com.sparksys.cache.template;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * description: redis缓存提供接口实现类
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:25:06
 */
@Slf4j
@SuppressWarnings({"unchecked", "ConstantConditions"})
public class RedisCacheTemplateImpl implements CacheTemplate {

    private static final Charset DEFAULT_CHARSET;
    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> valueOperations;


    static {
        DEFAULT_CHARSET = StandardCharsets.UTF_8;
    }

    @PostConstruct
    public void initRedisOperation() {
        valueOperations = redisTemplate.opsForValue();
    }

    public RedisCacheTemplateImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
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
        try {
            obj = (T) valueOperations.get(key);
            if (obj == null && function != null) {
                obj = function.apply(funcParam);
                if (obj != null) {
                    set(key, obj, expireTime);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return obj;
    }

    @Override
    public void set(String key, Object value) {
        set(key, value, null);
    }

    @Override
    public void set(String key, Object value, Long expireTime) {
        if (ObjectUtils.isNotEmpty(expireTime)) {
            valueOperations.set(key, value, expireTime);
        } else {
            valueOperations.set(key, value);
        }
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
    public Long remove(String... keys) {
        return redisTemplate.delete(Lists.newArrayList(keys));
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> redisConnection.exists(key.getBytes(DEFAULT_CHARSET)));
    }

    @Override
    public void flushDb() {
        redisTemplate.execute((RedisCallback<String>) (connection) -> {
            connection.flushDb();
            return "ok";
        });
    }

}
