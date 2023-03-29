package com.github.sparkzxl.gateway.plugin.autoconfigure;

import com.github.sparkzxl.gateway.plugin.exception.strategy.ConnectTimeoutExceptionHandlerStrategy;
import com.github.sparkzxl.gateway.plugin.exception.strategy.DefaultExceptionHandlerStrategy;
import com.github.sparkzxl.gateway.plugin.exception.strategy.ExceptionHandlerStrategy;
import com.github.sparkzxl.gateway.plugin.exception.strategy.NotFoundExceptionHandlerStrategy;
import com.github.sparkzxl.gateway.plugin.exception.strategy.ResponseStatusExceptionHandlerStrategy;
import com.github.sparkzxl.gateway.properties.GatewayPluginProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 异常策略配置
 *
 * @author zhouxinlei
 * @since 2022-01-10 15:14:16
 */
@Configuration
@ConditionalOnProperty(prefix = GatewayPluginProperties.PREFIX, value = "exception-json-handler", havingValue = "true")
public class ExceptionHandlerStrategyAutoConfig {

    @Bean
    public ExceptionHandlerStrategy connectTimeoutExceptionHandlerStrategy() {
        return new ConnectTimeoutExceptionHandlerStrategy();
    }

    @Bean
    public ExceptionHandlerStrategy defaultExceptionHandlerStrategy() {
        return new DefaultExceptionHandlerStrategy();
    }

    @Bean
    public ExceptionHandlerStrategy notFoundExceptionHandlerStrategy() {
        return new NotFoundExceptionHandlerStrategy();
    }

    @Bean
    public ExceptionHandlerStrategy responseStatusExceptionHandlerStrategy() {
        return new ResponseStatusExceptionHandlerStrategy();
    }

}
