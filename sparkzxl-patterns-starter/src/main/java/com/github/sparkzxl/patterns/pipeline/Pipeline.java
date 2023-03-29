package com.github.sparkzxl.patterns.pipeline;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Service;

/**
 * description: 责任链模式注解
 *
 * @author zhouxinlei
 * @since 2023-03-23 08:59:43
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface Pipeline {

    String value() default "";
}
