package com.github.sparkzxl.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: 操作日志注解
 *
 * @author zhouxinlei
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OptLogRecord {

    /**
     * 业务对象标识
     *
     * @return String
     */
    String bizNo() default "";

    /**
     * 操作日志的种类
     *
     * @return String
     */
    String category() default "";

    /**
     * 消息模板 el表达式变量#{[tenantId]}
     *
     * @return String
     */
    String template() default "";

    String extractParams() default "";

    /**
     * 表达式条件
     *
     * @return String
     */
    String expressionJson() default "";

    /**
     * 参数处理类beanName
     *
     * @return Class
     */
    String variablesBeanName() default "defaultOptOptLogVariablesHandler";

}
