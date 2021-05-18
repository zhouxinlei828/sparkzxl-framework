package com.github.sparkzxl.patterns.annonation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * description: 策略模式业务类型注解
 *
 * @author zhouxinlei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface BusinessStrategy {

    String type();

    String source();
}
