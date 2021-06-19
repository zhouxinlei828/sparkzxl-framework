package com.github.sparkzxl.core.generator;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * description:
 *
 * @author zhouxinlei
 */
public interface CacheKeyGenerator {

    /**
     * 获取AOP参数,生成指定缓存Key
     *
     * @param joinPoint 切入点
     * @return String
     */
    String getLockKey(ProceedingJoinPoint joinPoint);


}
