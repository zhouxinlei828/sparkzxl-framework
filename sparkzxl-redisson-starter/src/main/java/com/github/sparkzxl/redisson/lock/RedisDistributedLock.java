package com.github.sparkzxl.redisson.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * description：redis分布式锁
 *
 * @author zhouxinlei
 * @date 2020/6/10 0010
 */
@Slf4j
public class RedisDistributedLock extends AbstractDistributedLock {

    public RedissonClient redissonClient;

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean lock(String key, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(key);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean lock(String key, long waitTime, long leaseTime, int tryCount, long sleepTime) {
        boolean result = false;
        try {
            boolean unlock = lock(key, waitTime, leaseTime);
            log.info("======> " + Thread.currentThread() + "尝试{}获取锁{} -> {}", tryCount, key, unlock);
            if (unlock) {
                result = true;
            } else {
                while (tryCount > 0) {
                    Thread.sleep(sleepTime);
                    --tryCount;
                    boolean res = lock(key, waitTime, leaseTime, tryCount, sleepTime);
                    if (res) {
                        result = true;
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("RedisLock 获取分布式锁异常 -> {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        return result;
    }

    @Override
    public void releaseLock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (lock.isLocked()) {
            lock.unlock();
        }
    }


    /**
     * 初始红包数量
     *
     * @param key
     * @param count
     */
    public void initCount(String key, int count) {
        RMapCache<String, Integer> mapCache = redissonClient.getMapCache("skill");
        mapCache.putIfAbsent(key, count, 3, TimeUnit.DAYS);
    }

    /**
     * 递增
     *
     * @param key   key值
     * @param delta 要增加几(大于0)
     * @return int
     */
    public int incr(String key, int delta) {
        RMapCache<String, Integer> mapCache = redissonClient.getMapCache("skill");
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        //加1并获取计算后的值
        return mapCache.addAndGet(key, 1);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return int
     */
    public int decr(String key, int delta) {
        RMapCache<String, Integer> mapCache = redissonClient.getMapCache("skill");
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        //加1并获取计算后的值
        return mapCache.addAndGet(key, -delta);
    }
}
