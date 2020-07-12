package com.sparksys.commons.core.lock;

/**
 * description：
 *
 * @author zhouxinlei
 * @date  2020/6/17 0017
 */
public interface DistributedLock {

    int WAIT_TIME = 100;
    long SLEEP_MILLIS = 100L;

    boolean lock(String key);

    boolean lock(String key, int waitTime);

    boolean lock(String key, int waitTime, long leaseTime);

    boolean lock(String key, long leaseTime);

    boolean releaseLock(String key);
}
