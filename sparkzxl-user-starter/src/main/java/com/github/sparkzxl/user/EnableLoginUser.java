package com.github.sparkzxl.user;

import java.lang.annotation.*;

/**
 * description: 是否启用自动获取用户信息注解
 *
 * @author zhouxinlei
 * @since 2023-06-07 11:28:53
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableLoginUser {
}
