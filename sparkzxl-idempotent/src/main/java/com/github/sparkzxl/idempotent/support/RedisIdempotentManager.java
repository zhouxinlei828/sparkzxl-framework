package com.github.sparkzxl.idempotent.support;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * description: redis 幂等处理
 *
 * @author zhouxinlei
 */
public class RedisIdempotentManager implements IdempotentManager {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ValueOperations<String, Object> valueOperations;

    private final String pid = ManagementFactory.getRuntimeMXBean().getName();

    public RedisIdempotentManager(RedisTemplate<String, Object> redisTemplate) {
        Assert.notNull(redisTemplate, "redisTemplate 不能为空");
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public boolean tryLock(IdempotentInfo idempotentInfo) {
        return Boolean.TRUE.equals(valueOperations
                .setIfAbsent(idempotentInfo.getKey(), generatorValue(),
                        idempotentInfo.getMaxLockMilli(), TimeUnit.MILLISECONDS));
    }

    @Override
    public Object handlerNoLock(IdempotentInfo idempotentInfo) {
        throw new IdempotentNoLockException(idempotentInfo.getMessage());
    }

    @Override
    public void complete(IdempotentInfo idempotentInfo, Object result) {
        redisTemplate.delete(idempotentInfo.getKey());
    }

    private String generatorValue() {
        return pid + "_" + Thread.currentThread().getName();
    }
}
