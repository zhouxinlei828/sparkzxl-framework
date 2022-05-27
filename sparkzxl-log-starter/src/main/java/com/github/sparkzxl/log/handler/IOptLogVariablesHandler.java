package com.github.sparkzxl.log.handler;

import com.github.sparkzxl.log.annotation.OptLogRecord;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * description: 获取日志变量参数
 *
 * @author zhouxinlei
 */
public interface IOptLogVariablesHandler {


    /**
     * 获取日志变量Map
     *
     * @param method       method
     * @param args         args
     * @param optLogRecord 日志注解
     * @return Map<String, Object>
     */
    Map<String, Object> getVariables(Method method, Object[] args, OptLogRecord optLogRecord);
}
