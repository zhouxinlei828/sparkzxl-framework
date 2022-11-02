package com.github.sparkzxl.database;

import java.lang.annotation.*;

/**
 * description: 数据权限注解
 *
 * @author zhouxinlei
 * @since 2022-07-18 11:23:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@Documented
public @interface DataScope {

    /**
     * 数据权限id {@link org.phoenix.database.properties.DataProperties.DataScope#getScopeId();}
     */
    String value() default "";

}
