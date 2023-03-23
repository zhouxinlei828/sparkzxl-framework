package com.github.sparkzxl.patterns.strategy;

import com.google.common.collect.Maps;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Map;

/**
 * description: 策略上下文
 *
 * @author zhouxinlei
 * @since 2023-03-23 13:50:46
 */
public class BusinessStrategyContext {

    private final Map<BusinessStrategy, BusinessHandler> strategyContainer = Maps.newHashMap();

    public <R, T> R callStrategy(String type, String source, Object arg0) {
        BusinessStrategy businessStrategy = new BusinessStrategyImpl(type, source);
        BusinessHandler<R, T> businessHandler = strategyContainer.get(businessStrategy);
        return businessHandler.execute((T) arg0);
    }

    public BusinessStrategyContext add(BusinessHandler businessHandler) {
        BusinessStrategy businessStrategy = AnnotationUtils.findAnnotation(businessHandler.getClass(), BusinessStrategy.class);
        this.strategyContainer.put(businessStrategy, businessHandler);
        return this;
    }
}
