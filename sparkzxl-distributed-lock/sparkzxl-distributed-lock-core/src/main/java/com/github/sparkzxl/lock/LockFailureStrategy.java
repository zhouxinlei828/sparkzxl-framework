package com.github.sparkzxl.lock;

import java.lang.reflect.Method;

/**
 * description: 锁失败策略
 *
 * @author zhouxinlei
 * @since 2022-05-01 21:45:05
 */
public interface LockFailureStrategy {

    /**
     * 锁失败事件
     *
     * @param key       锁key
     * @param method    方法
     * @param arguments 参数
     */
    void onLockFailure(String key, Method method, Object[] arguments);
}
