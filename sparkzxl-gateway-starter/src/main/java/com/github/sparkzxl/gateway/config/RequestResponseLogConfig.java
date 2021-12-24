package com.github.sparkzxl.gateway.config;

import com.github.sparkzxl.gateway.filter.log.AccessLogFilter;
import com.github.sparkzxl.gateway.filter.log.ResponseLogCachedBodyFilter;
import com.github.sparkzxl.gateway.properties.LogRequestProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 日志配置
 *
 * @author zhoux
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = LogRequestProperties.GATEWAY_PLUGIN_LOG_PROPERTIES_PREFIX, value = "enabled", havingValue = "true")
public class RequestResponseLogConfig {

    @Bean
    @ConditionalOnMissingBean(AccessLogFilter.class)
    public AccessLogFilter accessLogFilter() {
        return new AccessLogFilter();
    }

    @Bean
    @ConditionalOnMissingBean(ResponseLogCachedBodyFilter.class)
    public ResponseLogCachedBodyFilter responseLogCachedBodyFilter(ApplicationContext applicationContext) {
        return new ResponseLogCachedBodyFilter(applicationContext);
    }

}
