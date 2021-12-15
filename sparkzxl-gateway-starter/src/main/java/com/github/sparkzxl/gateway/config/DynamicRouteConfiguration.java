package com.github.sparkzxl.gateway.config;

import com.github.sparkzxl.gateway.predicate.processor.DefaultDynamicRouteProcessor;
import com.github.sparkzxl.gateway.predicate.processor.DynamicRouteProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 动态路由配置
 *
 * @author zhoux
 */
@Configuration
@Slf4j
public class DynamicRouteConfiguration {

    @Bean
    @ConditionalOnMissingBean(DynamicRouteProcessor.class)
    public DynamicRouteProcessor dynamicRoutePreprocessor() {
        return new DefaultDynamicRouteProcessor();
    }

}
