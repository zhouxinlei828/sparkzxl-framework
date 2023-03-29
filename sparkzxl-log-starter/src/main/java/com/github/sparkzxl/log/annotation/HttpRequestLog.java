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
     * 是否启用 操作日志 禁用控制优先级：lamp.log.enabled = false > 控制器类上@SysLog(enabled = false) > 控制器方法上@SysLog(enabled = false)
     *
     * @return 是否启用
     */
    boolean enabled() default true;

    /**
     * 业务类型
     */
    String value() default "";

    /**
     * 是否记录方法的入参
     *
     * @return 是否记录方法的入参
     */
    boolean request() default true;

    /**
     * 是否记录返回值
     *
     * @return 是否记录返回值
     */
    boolean response() default true;

}
