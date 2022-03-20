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
     * 是否启用 操作日志
     * 禁用控制优先级：lamp.log.enabled = false > 控制器类上@SysLog(enabled = false) > 控制器方法上@SysLog(enabled = false)
     *
     * @return 是否启用
     */
    boolean enabled() default true;

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

    /**
     * 是否记录方法的入参
     *
     * @return 是否记录方法的入参
     */
    boolean request() default true;

    /**
     * 若设置了 request = false、requestByError = true，则方法报错时，依然记录请求的入参
     *
     * @return 当 request = false时， 方法报错记录请求参数
     */
    boolean requestByError() default true;

    /**
     * 是否记录返回值
     *
     * @return 是否记录返回值
     */
    boolean response() default true;


}
