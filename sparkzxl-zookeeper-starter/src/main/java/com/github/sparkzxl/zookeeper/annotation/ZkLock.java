package com.github.sparkzxl.zookeeper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: redis lock注解
 *
 * @author zhouxinlei
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ZkLock {

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

}
