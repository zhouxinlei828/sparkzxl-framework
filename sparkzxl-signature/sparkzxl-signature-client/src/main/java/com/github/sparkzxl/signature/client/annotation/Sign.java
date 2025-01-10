package com.github.sparkzxl.signature.client.annotation;

import com.github.sparkzxl.signature.client.interceptor.BaseSignResultInterceptor;
import com.github.sparkzxl.signature.client.interceptor.SignResultInterceptor;

import java.lang.annotation.*;

/**
 * description: 加签注解
 *
 * @author zhouxinlei
 * @since 2022-07-18 11:23:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@Documented
public @interface Sign {

    /**
     * appKey
     *
     * @return String
     */
    String value() default "";

    Class<? extends SignResultInterceptor> handler() default BaseSignResultInterceptor.class;

}
