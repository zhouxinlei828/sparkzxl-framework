package com.github.sparkzxl.alarm.annotation;

import java.lang.annotation.*;

/**
 * description: 告警注解参数
 *
 * @author zhouxinlei
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AlarmParam {
    /**
     * 字段名称
     *
     * @return String
     */
    String name() default "";
}
