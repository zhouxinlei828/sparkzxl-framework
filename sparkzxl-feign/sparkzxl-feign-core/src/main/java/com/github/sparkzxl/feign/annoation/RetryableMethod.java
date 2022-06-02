package com.github.sparkzxl.feign.annoation;

import java.lang.annotation.*;

/**
 * description: 标注这个 feign 方法或者 feign 类里面的所有方法都是可以重试
 *
 * @author zhouxinlei
 * @since 2022-04-04 11:51:31
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RetryableMethod {
}
