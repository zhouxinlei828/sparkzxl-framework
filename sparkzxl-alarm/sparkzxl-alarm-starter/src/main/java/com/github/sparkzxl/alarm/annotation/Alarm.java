package com.github.sparkzxl.alarm.annotation;

import com.github.sparkzxl.alarm.constant.AlarmConstant;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 告警注解
 *
 * @author zhouxinlei
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Alarm {

    /**
     * 机器人id
     *
     * @return String
     */
    String robotId() default "";

    /**
     * 报警名称
     *
     * @return String
     */
    String name() default "";

    MessageSubType messageType() default MessageSubType.TEXT;

    String templateId() default "";

    String variablesBeanName() default AlarmConstant.DEFAULT_ALARM_VARIABLES_HANDLER_BEAN_NAME;

    String extractParams() default "";

    /**
     * 表达式条件
     *
     * @return String
     */
    String expressionJson() default "";


}
