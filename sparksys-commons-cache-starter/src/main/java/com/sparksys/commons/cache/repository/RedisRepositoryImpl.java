package com.sparksys.commons.cache.repository;

import com.sparksys.commons.cache.utils.RedisObjectSerializer;
import com.sparksys.commons.core.repository.CacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

/**
 * description: redis缓存提供接口实现类
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:25:06
 */
@Slf4j
@SuppressWarnings({"unchecked", "ConstantConditions"})
public class RedisRepositoryImpl implements CacheRepository {

    private static final Charset DEFAULT_CHARSET;
    private static final RedisObjectSerializer OBJECT_SERIALIZER;

    private final RedisTemplate<String, Object> redisTemplate;

    static {
        DEFAULT_CHARSET = StandardCharsets.UTF_8;
        OBJECT_SERIALIZER = new RedisObjectSerializer();
    }

    public RedisRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    protected RedisSerializer<String> getRedisSerializer() {
        return this.redisTemplate.getStringSerializer();
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
            obj = this.redisTemplate.execute((RedisCallback<T>) redisConnection -> {
                byte[] keys = this.getRedisSerializer().serialize(key);
                assert keys != null;
                byte[] values = redisConnection.get(keys);
                return (T) OBJECT_SERIALIZER.deserialize(values);
            });
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
        this.redisTemplate.execute((RedisCallback<Long>) (connection) -> {
            byte[] keys = this.getRedisSerializer().serialize(key);
            byte[] values = OBJECT_SERIALIZER.serialize(value);
            assert keys != null;
            assert values != null;
            connection.set(keys, values);
            if (expireTime == null) {
                connection.set(keys, values);
            } else {
                connection.setEx(keys, expireTime, values);
            }
            return 1L;
        });
    }

    @Override
    public Long increment(String key) {
        return this.redisTemplate.execute((RedisCallback<Long>) (connection) -> connection.incr(this.getRedisSerializer().serialize(key)));
    }

    @Override
    public Long increment(String key, long delta) {
        return this.redisTemplate.execute((RedisCallback<Long>) (connection) -> connection.incrBy(this.getRedisSerializer().serialize(key), delta));
    }

    @Override
    public Long decrement(String key) {
        return this.redisTemplate.execute((RedisCallback<Long>) (connection) -> connection.decr(this.getRedisSerializer().serialize(key)));
    }

    @Override
    public Long decrement(String key, long delta) {
        return this.redisTemplate.execute((RedisCallback<Long>) (connection) -> connection.decrBy(this.getRedisSerializer().serialize(key), delta));
    }

    @Override
    public Long remove(String... keys) {
        LongAdder count = new LongAdder();
        return this.redisTemplate.execute((RedisCallback<Long>) (connection) -> {
            for (String key : keys) {
                count.add(connection.del(new byte[][]{key.getBytes(DEFAULT_CHARSET)}));
            }
            return count.longValue();
        });
    }

    @Override
    public boolean exists(String key) {
        return this.redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> redisConnection.exists(key.getBytes(DEFAULT_CHARSET)));
    }

    @Override
    public void flushDb() {
        this.redisTemplate.execute((RedisCallback<String>) (connection) -> {
            connection.flushDb();
            return "ok";
        });
    }

}
