package com.github.sparkzxl.lock.executor;

/**
 * description: 分布式锁核心抽象执行处理器
 *
 * @author zhouxinlei
 * @since 2022-03-26 17:18:40
 */
public abstract class AbstractLockExecutor<T> implements LockExecutor<T> {

    protected T obtainLockInstance(boolean locked, T lockInstance) {
        return locked ? lockInstance : null;
    }

}
