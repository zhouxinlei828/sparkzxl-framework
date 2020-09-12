package com.github.sparkzxl.patterns.annonation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * description: 策略模式业务类型注解
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 14:29:07
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface HandlerType {

    String type();

    String source();
}
