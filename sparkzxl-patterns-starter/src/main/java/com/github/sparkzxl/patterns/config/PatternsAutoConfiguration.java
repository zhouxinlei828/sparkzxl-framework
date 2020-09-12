package com.github.sparkzxl.patterns.config;

import com.github.sparkzxl.patterns.strategy.BusinessHandler;
import com.github.sparkzxl.patterns.strategy.BusinessHandlerChooser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * description: 设计模式自动注入配置
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 15:13:16
 */
@Configuration
public class PatternsAutoConfiguration {

    @Bean
    public BusinessHandlerChooser businessHandlerChooser(List<BusinessHandler> businessHandlers) {
        BusinessHandlerChooser businessHandlerChooser = new BusinessHandlerChooser();
        businessHandlerChooser.setBusinessHandlerMap(businessHandlers);
        return businessHandlerChooser;
    }
}
