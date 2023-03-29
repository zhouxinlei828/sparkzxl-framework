package com.github.sparkzxl.lock;

import com.github.sparkzxl.lock.exception.LockFailureException;
import java.lang.reflect.Method;

/**
 * description: 默认锁失败策略
 *
 * @author zhouxinlei
 * @since 2022-05-01 21:45:42
 */
public class DefaultLockFailureStrategy implements LockFailureStrategy {

    protected static String DEFAULT_MESSAGE = "request failed,please retry it.";


    @Override
    public void onLockFailure(String key, Method method, Object[] arguments) {
        throw new LockFailureException(DEFAULT_MESSAGE);
    }
}
