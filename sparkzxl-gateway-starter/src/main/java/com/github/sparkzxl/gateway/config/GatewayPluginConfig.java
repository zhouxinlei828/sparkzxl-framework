package com.github.sparkzxl.gateway.config;

import com.github.sparkzxl.gateway.filter.context.GatewayRequestContextFilter;
import com.github.sparkzxl.gateway.filter.context.RemoveGatewayContextFilter;
import com.github.sparkzxl.gateway.properties.GatewayPluginProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 网关插件配置
 *
 * @author zhoux
 * @date 2021-10-23 17:28:31
 */
@Slf4j
@Configuration
public class GatewayPluginConfig {

    @Bean
    @ConditionalOnMissingBean(GatewayPluginProperties.class)
    @ConfigurationProperties(GatewayPluginProperties.GATEWAY_PLUGIN_PROPERTIES_PREFIX)
    public GatewayPluginProperties gatewayPluginProperties() {
        return new GatewayPluginProperties();
    }

    @Bean
    @ConditionalOnBean(GatewayPluginProperties.class)
    @ConditionalOnMissingBean(GatewayRequestContextFilter.class)
    public GatewayRequestContextFilter gatewayContextFilter(@Autowired GatewayPluginProperties gatewayPluginProperties) {
        GatewayRequestContextFilter gatewayRequestContextFilter = new GatewayRequestContextFilter(gatewayPluginProperties);
        log.debug("Load GatewayContextFilter Config Bean");
        return gatewayRequestContextFilter;
    }

    @Bean
    @ConditionalOnBean(GatewayPluginProperties.class)
    @ConditionalOnMissingBean(RemoveGatewayContextFilter.class)
    public RemoveGatewayContextFilter removeGatewayContextFilter() {
        RemoveGatewayContextFilter gatewayContextFilter = new RemoveGatewayContextFilter();
        log.debug("Load RemoveGatewayContextFilter Config Bean");
        return gatewayContextFilter;
    }

}
