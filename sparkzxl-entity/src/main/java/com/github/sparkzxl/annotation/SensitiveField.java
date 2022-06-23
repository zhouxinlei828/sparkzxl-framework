package com.github.sparkzxl.annotation;


import cn.hutool.core.util.DesensitizedUtil;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * description: 加在敏感字段字段上，实现脱敏数据
 *
 * @author zhouxinlei
 * @since 2021-06-25 22:17:06
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface SensitiveField {

    DesensitizedUtil.DesensitizedType type() default DesensitizedUtil.DesensitizedType.PASSWORD;

}
