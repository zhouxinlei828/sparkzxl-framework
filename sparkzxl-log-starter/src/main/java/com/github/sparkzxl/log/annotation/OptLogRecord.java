package com.github.sparkzxl.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 操作日志注解
 *
 * @author zhouxinlei
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OptLogRecord {

    /**
     * 操作人
     *
     * @return String
     */
    String operator() default "";

    /**
     * 业务对象标识
     *
     * @return String
     */
    String bizNo() default "";

    /**
     * 操作日志的种类
     *
     * @return String
     */
    String category() default "";

    /**
     * 扩展参数，记录操作日志的修改详情
     *
     * @return String
     */
    String detail() default "";

    /**
     * 记录日志的条件
     *
     * @return String
     */
    String condition() default "";
}
