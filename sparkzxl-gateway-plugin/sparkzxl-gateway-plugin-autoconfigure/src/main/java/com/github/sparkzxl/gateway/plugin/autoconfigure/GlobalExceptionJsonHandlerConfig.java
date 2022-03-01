package com.github.sparkzxl.gateway.plugin.autoconfigure;

import com.github.sparkzxl.gateway.plugin.properties.GatewayPluginProperties;
import com.github.sparkzxl.gateway.plugin.exception.ExceptionHandlerStrategyMethodProcessor;
import com.github.sparkzxl.gateway.plugin.exception.JsonExceptionHandler;
import com.github.sparkzxl.gateway.plugin.exception.factory.DefaultExceptionHandlerStrategyFactory;
import com.github.sparkzxl.gateway.plugin.exception.factory.ExceptionHandlerStrategyFactory;
import com.github.sparkzxl.gateway.plugin.exception.strategy.ExceptionHandlerStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;

/**
 * description: 网关全局异常配置
 *
 * @author zhouxinlei
 * @date 2022-01-10 15:13:55
 */
@Slf4j
@Configuration
@Import(ExceptionHandlerStrategyMethodProcessor.class)
@ConditionalOnProperty(prefix = GatewayPluginProperties.PREFIX, value = "exception-json-handler", havingValue = "true")
public class GlobalExceptionJsonHandlerConfig {

    /**
     * ExceptionHandlerStrategyFactory
     *
     * @param exceptionHandlerStrategyList 异常处理策略列表
     * @return ExceptionHandlerStrategyFactory
     */
    @Bean
    @ConditionalOnMissingBean(ExceptionHandlerStrategyFactory.class)
    public ExceptionHandlerStrategyFactory exceptionHandlerStrategyFactory(List<ExceptionHandlerStrategy> exceptionHandlerStrategyList) {
        DefaultExceptionHandlerStrategyFactory factory = new DefaultExceptionHandlerStrategyFactory();
        exceptionHandlerStrategyList.forEach(factory::addStrategy);
        log.debug("Load ExceptionHandler Strategy Factory Config Bean");
        return factory;
    }


    @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return new DefaultServerCodecConfigurer();
    }

    /**
     * ErrorWebExceptionHandler
     *
     * @param viewResolversProvider           viewResolversProvider
     * @param serverCodecConfigurer           serverCodecConfigurer
     * @param exceptionHandlerStrategyFactory exceptionHandlerStrategyFactory
     * @return ErrorWebExceptionHandler
     */
    @Primary
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                                             ServerCodecConfigurer serverCodecConfigurer,
                                                             ExceptionHandlerStrategyFactory exceptionHandlerStrategyFactory) {

        JsonExceptionHandler jsonExceptionHandler = new JsonExceptionHandler(exceptionHandlerStrategyFactory);
        jsonExceptionHandler.setViewResolvers(viewResolversProvider.getIfAvailable(Collections::emptyList));
        jsonExceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        jsonExceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        log.debug("Load Json Exception Handler Config Bean");
        return jsonExceptionHandler;
    }
}
