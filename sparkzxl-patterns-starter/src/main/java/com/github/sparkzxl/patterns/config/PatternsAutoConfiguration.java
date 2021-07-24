package com.github.sparkzxl.patterns.config;

import com.github.sparkzxl.patterns.duty.HandlerChainExecute;
import com.github.sparkzxl.patterns.duty.HandlerInterceptor;
import com.github.sparkzxl.patterns.strategy.BusinessHandler;
import com.github.sparkzxl.patterns.strategy.BusinessHandlerChooser;
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
    public BusinessHandlerChooser businessHandlerChooser(List<BusinessHandler> businessHandlers) {
        BusinessHandlerChooser businessHandlerChooser = new BusinessHandlerChooser();
        businessHandlerChooser.setBusinessHandlerMap(businessHandlers);
        return businessHandlerChooser;
    }

    @Bean
    public HandlerChainExecute handlerChainExecute(List<HandlerInterceptor> handlerInterceptorList) {
        HandlerChainExecute handlerChainExecute = new HandlerChainExecute();
        handlerChainExecute.setHandlerInterceptorMap(handlerInterceptorList);
        return handlerChainExecute;
    }

}
