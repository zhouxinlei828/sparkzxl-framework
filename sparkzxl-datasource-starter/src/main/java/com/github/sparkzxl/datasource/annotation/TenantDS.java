package com.github.sparkzxl.datasource.annotation;

import java.lang.annotation.*;

/**
 * description: 租户数据源切换
 *
 * @author zhouxinlei
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenantDS {

}
