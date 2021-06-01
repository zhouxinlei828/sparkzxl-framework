package com.github.sparkzxl.core.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * description: API幂等性注解
 *
 * @author zhouxinlei
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApiIdempotent {

    /**
     * key的前缀
     *
     * @return String
     */
    String prefix() default "";

    // 过期时间（毫秒）
    long expireMillis() default 60;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    String message() default "请勿重复提交!";

    /**
     * 分隔符（默认 :）
     * 生成的Key：N:SO1008:500
     *
     * @return String
     */
    String delimiter() default ":";

}
