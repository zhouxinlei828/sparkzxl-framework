package com.github.sparkzxl.lock.executor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * description: Redisson 分布式锁
 *
 * @author zhouxinlei
 * @since 2022-05-03 09:11:03
 */
@Slf4j
@RequiredArgsConstructor
public class RedissonLockExecutor extends AbstractLockExecutor<RLock> {

    private final RedissonClient redissonClient;

    @Override
    public boolean renewal() {
        return true;
    }

    @Override
    public RLock acquire(String lockKey, String lockValue, long expire, long acquireTimeout) {
        final RLock lockInstance = redissonClient.getLock(lockKey);
        try {
            final boolean locked = lockInstance.tryLock(acquireTimeout, expire, TimeUnit.MILLISECONDS);
            return obtainLockInstance(locked, lockInstance);
        } catch (InterruptedException e) {
            log.error("Redisson lock failed：", e);
            return null;
        }
    }

    @Override
    public boolean releaseLock(String key, String value, RLock lockInstance) {
        if (lockInstance.isHeldByCurrentThread()) {
            try {
                lockInstance.forceUnlockAsync().get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Redisson releaseLock failed：", e);
                return false;
            }
        }
        return false;
    }
}
