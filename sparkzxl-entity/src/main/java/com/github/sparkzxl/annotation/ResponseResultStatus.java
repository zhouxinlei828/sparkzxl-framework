package com.github.sparkzxl.annotation;

import java.lang.annotation.*;

/**
 * description: 全局异常状态注解
 *
 * @author zhouxinlei
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseResultStatus {

    int value() default 500;

    String reason() default "";
}
