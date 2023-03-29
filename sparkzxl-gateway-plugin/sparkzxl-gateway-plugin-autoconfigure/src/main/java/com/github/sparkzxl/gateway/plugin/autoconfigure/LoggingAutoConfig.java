package com.github.sparkzxl.gateway.plugin.autoconfigure;

import com.github.sparkzxl.gateway.plugin.logging.OptLogServiceImpl;
import com.github.sparkzxl.gateway.plugin.logging.RequestLogFilter;
import com.github.sparkzxl.gateway.plugin.logging.ResponseLogFilter;
import com.github.sparkzxl.gateway.plugin.logging.service.IOptLogService;
import com.github.sparkzxl.gateway.properties.GatewayPluginProperties;
import com.github.sparkzxl.gateway.properties.LoggingProperties;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 日志记录自动配置
 *
 * @author zhouxinlei
 * @since 2022-01-10 16:03:00
 */
@Configuration
@ConditionalOnProperty(prefix = LoggingProperties.PREFIX, value = "enabled", havingValue = "true")
@Slf4j
public class LoggingAutoConfig {

    @Resource
    private GatewayPluginProperties gatewayPluginProperties;

    @Bean
    public IOptLogService optLogService() {
        return new OptLogServiceImpl(gatewayPluginProperties.getLogging());
    }

    @Bean
    @ConditionalOnMissingBean(RequestLogFilter.class)
    public GlobalFilter requestLogFilter() {
        return new RequestLogFilter();
    }

    @Bean
    @ConditionalOnMissingBean(ResponseLogFilter.class)
    public GlobalFilter responseLogFilter(ApplicationContext applicationContext, IOptLogService optLogService) {
        return new ResponseLogFilter(applicationContext, optLogService);
    }
}
