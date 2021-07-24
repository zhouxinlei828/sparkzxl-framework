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
     * spel表达式
     */
    String expression() default "#p0";

    /**
     * key的前缀
     *
     * @return String
     */
    String keyPrefix() default "";

}
