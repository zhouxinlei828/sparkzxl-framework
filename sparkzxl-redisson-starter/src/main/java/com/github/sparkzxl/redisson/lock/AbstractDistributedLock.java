package com.github.sparkzxl.redisson.lock;

/**
 * description：
 *
 * @author zhouxinlei
 */
public abstract class AbstractDistributedLock implements DistributedLock {

    @Override
    public boolean lock(String key) {
        return this.lock(key, WAIT_TIME, LEASE_TIME);
    }

}
