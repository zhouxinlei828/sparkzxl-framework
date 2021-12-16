package com.github.sparkzxl.idempotent.annotation;

import java.lang.annotation.*;

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
    String keyPrefix() default "";

    // 过期时间（毫秒）
    long maxLockMilli() default 10000L;

    String generator() default "";

    String message() default "请勿重复提交!";

    /**
     * 分隔符（默认 :）
     * 生成的Key：N:SO1008:500
     *
     * @return String
     */
    String delimiter() default ":";

}
