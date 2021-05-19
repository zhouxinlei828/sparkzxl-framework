package com.github.sparkzxl.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * description: API幂等性注解
 *
 * @author zhouxinlei
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiIdempotent {

    /**
     * key唯一性值 spel表达式
     */
    String keyExpression() default "#p0";

    /**
     * key的前缀
     *
     * @return String
     */
    String keyPrefix() default "";

    // 过期时间（毫秒）
    long expireMillis() default 60;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    String message() default "请勿重复提交!";

}
