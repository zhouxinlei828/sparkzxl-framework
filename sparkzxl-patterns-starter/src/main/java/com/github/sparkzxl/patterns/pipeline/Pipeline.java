package com.github.sparkzxl.patterns.pipeline;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

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
