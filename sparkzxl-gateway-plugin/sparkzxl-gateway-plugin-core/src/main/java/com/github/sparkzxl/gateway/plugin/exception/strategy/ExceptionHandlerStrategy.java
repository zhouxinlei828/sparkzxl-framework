package com.github.sparkzxl.gateway.plugin.exception.strategy;


import com.github.sparkzxl.gateway.plugin.exception.result.ExceptionHandlerResult;

/**
 * description: 异常处理策略
 *
 * @author zhoux
 */
public interface ExceptionHandlerStrategy<T extends Throwable> {

    /**
     * get the exception class
     *
     * @return Class
     */
    Class<T> getHandleClass();

    /**
     * 处理异常
     *
     * @param throwable 异常
     * @return ExceptionHandlerResult
     */
    ExceptionHandlerResult handleException(Throwable throwable);

}
