package com.github.sparkzxl.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 多列数据权限注解
 *
 * @author zhouxinlei
 * @since 2022-07-18 11:23:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@Documented
public @interface DataScope {

    /**
     * 数据权限id {@link com.github.sparkzxl.mybatis.properties.DataScopeConfig#getScopeId();}
     */
    String value() default "";

}
