package com.github.sparkzxl.patterns.config;

import com.github.sparkzxl.patterns.duty.HandlerInterceptor;
import com.github.sparkzxl.patterns.factory.BusinessStrategyFactory;
import com.github.sparkzxl.patterns.factory.DefaultBusinessStrategyFactory;
import com.github.sparkzxl.patterns.factory.DefaultHandlerChainFactory;
import com.github.sparkzxl.patterns.factory.HandlerChainFactory;
import com.github.sparkzxl.patterns.strategy.BusinessHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * description: 设计模式自动注入配置
 *
 * @author zhouxinlei
 */
@Configuration
public class PatternsAutoConfiguration {

    @Bean
    public BusinessStrategyFactory businessStrategyFactory(@Autowired(required = false) List<BusinessHandler> businessHandlers) {
        DefaultBusinessStrategyFactory businessStrategyFactory = new DefaultBusinessStrategyFactory();
        if (CollectionUtils.isNotEmpty(businessHandlers)) {
            businessHandlers.forEach(businessStrategyFactory::addStrategy);
        }
        return businessStrategyFactory;
    }

    @Bean
    public HandlerChainFactory handlerChainFactory(@Autowired(required = false) List<HandlerInterceptor> handlerInterceptorList) {
        DefaultHandlerChainFactory handlerChainFactory = new DefaultHandlerChainFactory();
        if (CollectionUtils.isNotEmpty(handlerInterceptorList)) {
            handlerInterceptorList.forEach(handlerChainFactory::addInterceptor);
        }
        return handlerChainFactory;
    }
}
