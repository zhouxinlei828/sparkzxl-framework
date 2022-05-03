package com.github.sparkzxl.lock;

import org.aspectj.lang.JoinPoint;

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
     * @param joinPoint      切入点
     * @param definitionKeys 定义
     * @return key
     */
    String buildKey(JoinPoint joinPoint, String[] definitionKeys);

}
