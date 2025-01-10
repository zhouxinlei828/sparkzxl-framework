package com.github.sparkzxl.signature.client.annotation;

import java.lang.annotation.*;

/**
 * description: 加签字段注解
 *
 * @author zhouxinlei
 * @since 2022-07-18 11:23:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER, ElementType.FIELD})
@Documented
public @interface SignField {

}
