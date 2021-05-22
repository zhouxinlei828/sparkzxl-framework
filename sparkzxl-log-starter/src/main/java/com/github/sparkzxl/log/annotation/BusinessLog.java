package com.github.sparkzxl.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 业务请求日志注解
 *
 * @author zhouxinlei
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessLog {

    /**
     * 业务类型
     *
     * @return String
     */
    String type() default "";

    /**
     * value值
     *
     * @return String
     */
    String value() default "";
}
