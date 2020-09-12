package com.github.sparkzxl.boot.infrastructure.annonation;

import com.github.sparkzxl.core.spring.SpringContextUtils;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * description: 自动注入SpringUtil
 *
 * @author: zhouxinlei
 * @date: 2020-08-02 14:26:02
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SpringContextUtils.class)
public @interface EnableSpringUtil {
}
