package com.github.sparkzxl.log.aspect;

import com.github.sparkzxl.log.annotation.HttpRequestLog;
import org.aspectj.lang.JoinPoint;

import java.util.Map;

/**
 * description: 获取日志变量参数
 *
 * @author zhouxinlei
 */
public interface ILogAttribute {


    /**
     * 获取日志变量Map
     *
     * @param joinPoint      切入点
     * @param httpRequestLog 日志注解
     * @return Map<String, Object>
     */
    Map<String, Object> getAttributes(JoinPoint joinPoint, HttpRequestLog httpRequestLog);
}
