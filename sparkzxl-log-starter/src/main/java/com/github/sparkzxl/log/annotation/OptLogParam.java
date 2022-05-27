package com.github.sparkzxl.log.annotation;


import java.lang.annotation.*;

/**
 * description: 请求日志注解参数
 *
 * @author zhouxinlei
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OptLogParam {

    /**
     * 字段名称
     *
     * @return String
     */
    String value() default "";

}
