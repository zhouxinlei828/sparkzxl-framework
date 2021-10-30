package com.github.sparkzxl.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: web请求日志注解
 *
 * @author zhouxinlei
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRequestLog {

    /**
     * 业务类型
     */
    String value() default "";

    /**
     * 分隔符（默认 :）
     * 生成的Key：业务类型:500
     *
     * @return String
     */
    String delimiter() default ":";

}
