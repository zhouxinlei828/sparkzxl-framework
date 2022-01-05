package com.github.sparkzxl.alarm;

import com.github.sparkzxl.alarm.annotation.Alarm;
import org.aspectj.lang.JoinPoint;

import java.util.Map;

/**
 * description: 获取告警变量参数
 *
 * @author zhouxinlei
 * @date 2022-01-05 12:21:58
 */
public interface IAlarmAttributeGet {


    /**
     * 获取告警变量Map
     *
     * @param joinPoint 切入点
     * @param alarm     告警注解
     * @return Map<String, String>
     */
    Map<String, Object> getAttributes(JoinPoint joinPoint, Alarm alarm);
}
