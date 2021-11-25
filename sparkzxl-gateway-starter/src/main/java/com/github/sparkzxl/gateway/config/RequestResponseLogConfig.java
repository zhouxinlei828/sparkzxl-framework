package com.github.sparkzxl.gateway.config;

import com.github.sparkzxl.gateway.filter.RequestLogFilter;
import com.github.sparkzxl.gateway.filter.ResponseLogFilter;
import com.github.sparkzxl.gateway.properties.GatewayPluginProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 日志配置
 *
 * @author zhoux
 * @date 2021-10-23 17:27:30
 */
@Slf4j
@Configuration
public class RequestResponseLogConfig {

    @Bean
    @ConditionalOnMissingBean(RequestLogFilter.class)
    @ConditionalOnProperty(prefix = GatewayPluginProperties.GATEWAY_PLUGIN_PROPERTIES_PREFIX, value = "log-request", havingValue = "true")
    public RequestLogFilter requestLogFilter() {
        RequestLogFilter requestLogFilter = new RequestLogFilter();
        log.info("Load Request Log Filter Config Bean");
        return requestLogFilter;
    }

    @Bean
    @ConditionalOnMissingBean(ResponseLogFilter.class)
    @ConditionalOnProperty(prefix = GatewayPluginProperties.GATEWAY_PLUGIN_PROPERTIES_PREFIX, value = "log-request", havingValue = "true")
    public ResponseLogFilter responseLogFilter() {
        ResponseLogFilter responseLogFilter = new ResponseLogFilter();
        log.info("Load Response Log Filter Config Bean");
        return responseLogFilter;
    }

}
