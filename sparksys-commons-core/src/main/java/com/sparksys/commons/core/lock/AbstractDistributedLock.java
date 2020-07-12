package com.sparksys.commons.core.lock;

/**
 * description：
 *
 * @author zhouxinlei
 * @date  2020/6/17 0017
 */
public abstract class AbstractDistributedLock implements DistributedLock {

    @Override
    public boolean lock(String key) {
        return this.lock(key, WAIT_TIME, SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, long leaseTime) {
        return this.lock(key, WAIT_TIME, leaseTime);
    }

    @Override
    public boolean lock(String key, int waitTime) {
        return this.lock(key, waitTime, SLEEP_MILLIS);
    }
}
