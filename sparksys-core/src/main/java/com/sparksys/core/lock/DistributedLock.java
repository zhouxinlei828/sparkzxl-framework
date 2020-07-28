package com.sparksys.core.lock;

/**
 * description：
 *
 * @author zhouxinlei
 * @date 2020/6/17 0017
 */
public interface DistributedLock {

    int WAIT_TIME = 100;
    long SLEEP_MILLIS = 100L;

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
     * @param key      key值
     * @param waitTime 等待时间
     * @return boolean
     */
    boolean lock(String key, int waitTime);

    /**
     * 加锁
     *
     * @param key       key值
     * @param waitTime  等待时间
     * @param leaseTime 最长时间
     * @return boolean
     */
    boolean lock(String key, int waitTime, long leaseTime);

    /**
     * 加锁
     *
     * @param key       key值
     * @param leaseTime 最长时间
     * @return boolean
     */
    boolean lock(String key, long leaseTime);

    /**
     * 释放锁
     *
     * @param key key值
     * @return boolean
     */
    boolean releaseLock(String key);
}
