package com.github.sparkzxl.redisson.lock;

/**
 * description：分布式锁接口
 *
 * @author zhouxinlei
 * @date 2020/6/17 0017
 */
public interface DistributedLock {

    long WAIT_TIME = 1;
    long LEASE_TIME = 1000;

    /**
     * 加锁
     *
     * @param key key值
     * @return boolean
     */
    boolean lock(String key);

    /**
     * 加锁
     *
     * @param key       key值
     * @param waitTime  等待时间
     * @param leaseTime 最长时间
     * @return boolean
     */
    boolean lock(String key, long waitTime, long leaseTime);
    /**
     * 加锁
     *
     * @param key       key值
     * @param waitTime  等待时间
     * @param leaseTime 最长时间
     * @param tryCount  重试次数
     * @param sleepTime 重试休眠时长（毫秒）
     * @return boolean
     */

    boolean lock(String key, long waitTime, long leaseTime, int tryCount, long sleepTime);

    /**
     * 释放锁
     *
     * @param key key值
     */
    void releaseLock(String key);
}
