package com.sparksys.commons.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: API幂等性注解
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:40:39
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SysLog {

    String value() default "head";
}
