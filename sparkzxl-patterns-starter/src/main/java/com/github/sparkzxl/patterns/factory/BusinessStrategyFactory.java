package com.github.sparkzxl.patterns.factory;

import com.github.sparkzxl.patterns.strategy.BusinessHandler;

/**
 * description: 业务处理工厂
 *
 * @author zhoux
 * @date 2021-10-23 12:42:23
 */
public interface BusinessStrategyFactory {

    /**
     * 获取业务策略
     *
     * @param type   业务类型
     * @param source 业务属性条件
     * @param <R>    返回值
     * @param <T>    入参
     * @return BusinessHandler<R, T>
     */
    <R, T> BusinessHandler<R, T> getStrategy(String type, String source);

}
