package com.github.sparkzxl.core.annotation;

import com.github.sparkzxl.core.encrypt.DefaultEncryptFormatter;
import com.github.sparkzxl.core.encrypt.EncryptFormatter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * description: 信息加密注解
 *
 * @author zhouxinlei
 * @date 2021-06-25 22:25:04
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface Encrypted {

    Class<? extends EncryptFormatter> formatter() default DefaultEncryptFormatter.class;

    boolean ignored() default false;

}
