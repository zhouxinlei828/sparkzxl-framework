package com.github.sparkzxl.lock;

import org.aopalliance.intercept.MethodInvocation;

/**
 * description: 锁key构建
 *
 * @author zhouxinlei
 * @since 2022-05-01 21:48:00
 */
public interface LockKeyBuilder {

    /**
     * 构建key
     *
     * @param invocation     invocation
     * @param definitionKeys 定义
     * @return key
     */
    String buildKey(MethodInvocation invocation, String[] definitionKeys);

}
