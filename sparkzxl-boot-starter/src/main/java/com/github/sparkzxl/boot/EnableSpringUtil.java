package com.github.sparkzxl.boot;

import com.github.sparkzxl.core.spring.SpringContextUtils;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

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
