package com.github.sparkzxl.zookeeper.lock;

/**
 * description：
 *
 * @author zhouxinlei
 * @date 2020/6/17 0017
 */
public interface DistributedLock {

    /**
     * 加锁
     *
     * @param key key值
     * @return boolean
     */
    boolean lock(String key);

    /**
     * 释放锁
     *
     * @param key key值
     * @return boolean
     */
    boolean releaseLock(String key);
}
