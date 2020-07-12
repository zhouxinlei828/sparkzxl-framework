package com.sparksys.commons.zookeeper.lock;

/**
 * description：
 *
 * @author zhouxinlei
 * @date 2020/6/17 0017
 */
public interface DistributedLock {

    boolean lock(String key);

    boolean releaseLock(String key);
}
