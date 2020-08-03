package com.sparksys.patterns.config;

import com.sparksys.patterns.strategy.BusinessHandlerChooser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 设计模式自动注入配置
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 15:13:16
 */
@Configuration
public class PatternsAutoConfiguration {

    @Bean
    public BusinessHandlerChooser businessHandlerChooser() {
        return new BusinessHandlerChooser();
    }
}
