package com.github.sparkzxl.redisson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: redis lock注解
 *
 * @author: zhouxinlei
 * @date: 2021-01-04 09:57:20
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {

    /**
     * 特定参数识别，默认取第 0 个下标
     */
    int lockFiled() default 0;

    /**
     * key的前缀
     *
     * @return String
     */
    String keyPrefix() default "";

    /**
     * 获取锁等待时长（毫秒）
     *
     * @return int
     */
    long waitTime() default 50;

    /**
     * 获取锁后自动过期时长（毫秒）
     *
     * @return int
     */
    long leaseTime() default 10000;

    /**
     * 重试次数
     *
     * @return int
     */
    int tryCount() default 0;

    /**
     * 重试休眠时长（毫秒）
     *
     * @return int
     */
    long sleepTime() default 500;

}
