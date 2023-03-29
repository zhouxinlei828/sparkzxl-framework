package com.github.sparkzxl.gateway.plugin.exception.factory;

import com.github.sparkzxl.gateway.plugin.exception.strategy.DefaultExceptionHandlerStrategy;
import com.github.sparkzxl.gateway.plugin.exception.strategy.ExceptionHandlerStrategy;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * description: 异常处理工厂
 *
 * @author zhoux
 */
@Slf4j
public class DefaultExceptionHandlerStrategyFactory implements ExceptionHandlerStrategyFactory {

    private final Map<Class<? extends Throwable>, ExceptionHandlerStrategy> strategyContainer;

    private final ExceptionHandlerStrategy defaultExceptionHandlerStrategy;

    public DefaultExceptionHandlerStrategyFactory() {
        this(new DefaultExceptionHandlerStrategy());
    }

    public DefaultExceptionHandlerStrategyFactory(ExceptionHandlerStrategy defaultExceptionHandlerStrategy) {
        this.strategyContainer = new HashMap<>();
        this.defaultExceptionHandlerStrategy = defaultExceptionHandlerStrategy;
    }

    /**
     * 添加异常处理策略
     *
     * @param exceptionHandlerStrategy 异常处理策略
     */
    public void addStrategy(ExceptionHandlerStrategy exceptionHandlerStrategy) {
        Assert.notNull(exceptionHandlerStrategy, "ExceptionStrategy Required");
        Class clazz = exceptionHandlerStrategy.getHandleClass();
        Assert.notNull(clazz, "ExceptionStrategy Handle Class Required");
        if (!strategyContainer.containsKey(clazz)) {
            strategyContainer.put(clazz, exceptionHandlerStrategy);
            log.debug("[DefaultExceptionHandlerStrategyFactory] Add Strategy,Class:{},Strategy:{}", clazz, exceptionHandlerStrategy);
        }
    }

    @Override
    public ExceptionHandlerStrategy getStrategy(Class clazz) {
        ExceptionHandlerStrategy strategy = strategyContainer.get(clazz);
        if (null == strategy && null != this.defaultExceptionHandlerStrategy) {
            log.debug("[DefaultExceptionHandlerStrategyFactory]Get Target Exception Handler Strategy Is Null,Use Default Strategy");
            strategy = defaultExceptionHandlerStrategy;
        }
        log.debug("[DefaultExceptionHandlerStrategyFactory] Get Strategy,Exception Class Name:{},Strategy:{}", clazz.getSimpleName(),
                strategy.getClass().getSimpleName());
        return strategy;
    }

}
