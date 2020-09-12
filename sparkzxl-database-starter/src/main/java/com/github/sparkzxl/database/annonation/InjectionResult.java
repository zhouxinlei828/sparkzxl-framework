package com.github.sparkzxl.database.annonation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 自动注入数据返回值注入 注解
 *
 * @author: zhouxinlei
 * @date: 2020-07-19 08:48:32
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface InjectionResult {
}
