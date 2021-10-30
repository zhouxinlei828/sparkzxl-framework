package com.github.sparkzxl.gateway.response.factory;

import com.github.sparkzxl.gateway.response.strategy.ExceptionHandlerStrategy;

/**
 * description: 异常处理策略工厂
 *
 * @author zhoux
 * @date 2021-10-23 21:33:01
 */
public interface ExceptionHandlerStrategyFactory {

    /**
     * Get Strategy
     *
     * @param clazz
     * @return ExceptionHandlerStrategy
     */
    ExceptionHandlerStrategy getStrategy(Class<? extends Throwable> clazz);

}
