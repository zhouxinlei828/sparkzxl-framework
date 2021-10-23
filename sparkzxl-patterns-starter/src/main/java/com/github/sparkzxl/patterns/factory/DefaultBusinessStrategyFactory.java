package com.github.sparkzxl.patterns.factory;

import com.github.sparkzxl.patterns.annonation.BusinessStrategy;
import com.github.sparkzxl.patterns.strategy.BusinessHandler;
import com.github.sparkzxl.patterns.strategy.BusinessStrategyImpl;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Map;

@Slf4j
public class DefaultBusinessStrategyFactory implements BusinessStrategyFactory {

    private final Map<BusinessStrategy, BusinessHandler> strategyContainer;

    public DefaultBusinessStrategyFactory() {
        this.strategyContainer = Maps.newHashMap();
    }

    @Override
    public <R, T> BusinessHandler<R, T> getStrategy(String type, String source) {
        BusinessStrategy businessStrategy = new BusinessStrategyImpl(type, source);
        return strategyContainer.get(businessStrategy);
    }

    public DefaultBusinessStrategyFactory addStrategy(BusinessHandler businessHandler) {
        BusinessStrategy businessStrategy = AnnotationUtils.findAnnotation(businessHandler.getClass(), BusinessStrategy.class);
        this.strategyContainer.put(businessStrategy, businessHandler);
        return this;
    }
}
