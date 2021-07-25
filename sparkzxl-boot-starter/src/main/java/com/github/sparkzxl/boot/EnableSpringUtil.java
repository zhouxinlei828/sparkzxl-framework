package com.github.sparkzxl.boot;

import com.github.sparkzxl.core.spring.SpringContextUtils;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * description: 自动注入SpringUtil
 *
 * @author zhouxinlei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SpringContextUtils.class)
public @interface EnableSpringUtil {
}
