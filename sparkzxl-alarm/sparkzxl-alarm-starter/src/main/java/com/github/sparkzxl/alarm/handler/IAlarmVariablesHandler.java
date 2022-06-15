package com.github.sparkzxl.alarm.handler;

import com.github.sparkzxl.alarm.annotation.Alarm;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;

import java.util.Map;

/**
 * description: 告警处理变量参数
 *
 * @author zhouxinlei
 */
public interface IAlarmVariablesHandler {

    /**
     * 获取告警变量Map
     *
     * @param invocation 切入点
     * @param alarm      告警注解
     * @return Map<String, Object>
     */
    Map<String, Object> getVariables(MethodInvocation invocation, Alarm alarm);
}
