package com.github.sparkzxl.redisson.aspect;

import com.github.sparkzxl.redisson.annotation.RedisLock;
import com.github.sparkzxl.redisson.lock.RedisDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StringUtils;

/**
 * description: Redis分布式AOP实现
 *
 * @author zhouxinlei
 */
@Aspect
@Slf4j
public class RedisLockAspect {

    private final RedisDistributedLock redisDistributedLock;

    public RedisLockAspect(RedisDistributedLock redisDistributedLock) {
        this.redisDistributedLock = redisDistributedLock;
    }

    @Pointcut("@annotation(redisLock)")
    public void redisLockAspect(RedisLock redisLock) {

    }

    @Around(value = "redisLockAspect(redisLock)", argNames = "proceedingJoinPoint,redisLock")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, RedisLock redisLock) throws Throwable {
        long threadId = Thread.currentThread().getId();
        String keyPrefix = redisLock.prefix();
        if (StringUtils.isEmpty(keyPrefix)) {
            throw new RuntimeException("lock key don't null...");
        }
        long waitTime = redisLock.waitTime();
        long leaseTime = redisLock.leaseTime();
        int tryCount = redisLock.tryCount();
        long sleepTime = redisLock.sleepTime();
        String lockKey = LockKeyGenerator.getLockKey(proceedingJoinPoint);
        log.info("线程[{}] -> 要加锁的key：[{}]，value：[{}]", threadId, lockKey, lockKey);
        Object result;
        if (redisDistributedLock.lock(lockKey, waitTime, leaseTime, tryCount, sleepTime)) {
            log.info("线程[{}] -> 获取锁key[{}] 成功", threadId, lockKey);
            try {
                result = proceedingJoinPoint.proceed();
            } finally {
                log.info("线程[{}] -> 释放锁 key [{}]", threadId, lockKey);
                redisDistributedLock.releaseLock(lockKey);
            }
        } else {
            log.error("线程[{}] -> 获取锁key[{}] 失败", threadId, lockKey);
            throw new RuntimeException("哎呀，开了个小差，请稍后再试");
        }
        return result;
    }
}
