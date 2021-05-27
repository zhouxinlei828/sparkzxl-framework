package com.github.sparkzxl.redisson.annotation;

import java.lang.annotation.*;

/**
 * description: Redis锁注解参数
 *
 * @author zhouxinlei
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RedisLockParam {

    /**
     * 字段名称
     *
     * @return String
     */
    String name() default "";
}
