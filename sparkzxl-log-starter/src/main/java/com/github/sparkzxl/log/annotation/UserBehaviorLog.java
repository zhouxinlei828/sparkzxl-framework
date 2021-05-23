package com.github.sparkzxl.log.annotation;

import com.github.sparkzxl.log.DefaultUserBehaviorLogCallback;
import com.github.sparkzxl.log.DefaultUserBehaviorLogFormatter;
import com.github.sparkzxl.log.UserBehaviorLogCallback;
import com.github.sparkzxl.log.UserBehaviorLogFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 用户行为日志注解
 *
 * @author zhouxinlei
 * @date 2021-05-23 20:47:55
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserBehaviorLog {

    /**
     * spel表达式
     */
    String expression() default "#p0";

    /**
     * key的前缀
     *
     * @return String
     */
    String operation() default "";

    /**
     * content
     *
     * @return String
     */
    String content() default "";

    Class<? extends UserBehaviorLogFormatter> formatter() default DefaultUserBehaviorLogFormatter.class;

    Class<? extends UserBehaviorLogCallback> callback() default DefaultUserBehaviorLogCallback.class;


}
