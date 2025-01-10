package com.github.sparkzxl.signature.client.annotation;

import cn.hutool.core.date.DatePattern;

import java.lang.annotation.*;

/**
 * description: 加签数据时间注解
 *
 * @author zhouxinlei
 * @since 2022-07-18 11:23:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER, ElementType.FIELD})
@Documented
public @interface SignTime {

    Class<?> type() default String.class;

    String format() default DatePattern.NORM_DATETIME_PATTERN;

}
