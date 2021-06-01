package com.github.sparkzxl.core.annotation;

import java.lang.annotation.*;

/**
 * description: API幂等性注解参数
 *
 * @author zhouxinlei
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApiIdempotentParam {

    /**
     * 字段名称
     *
     * @return String
     */
    String name() default "";
}
