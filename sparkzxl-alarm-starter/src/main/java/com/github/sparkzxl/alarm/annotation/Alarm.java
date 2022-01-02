package com.github.sparkzxl.alarm.annotation;

import com.github.sparkzxl.alarm.constant.enums.MessageTye;

import java.lang.annotation.*;

/**
 * description: 告警注解
 *
 * @author zhouxinlei
 * @date 2021-12-28 09:02:31
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Alarm {

    /**
     * 报警名称
     *
     * @return String
     */
    String name() default "";

    MessageTye messageType() default MessageTye.TEXT;

    String templateCode() default "";

    String stateParam() default "state";

}
