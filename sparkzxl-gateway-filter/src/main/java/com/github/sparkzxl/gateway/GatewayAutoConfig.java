package com.github.sparkzxl.gateway;

import com.github.sparkzxl.gateway.filter.TraceFilter;
import com.github.sparkzxl.gateway.filter.WhiteListFilter;
import com.github.sparkzxl.gateway.properties.WhiteProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 网关配置
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties({WhiteProperties.class})
public class GatewayAutoConfig {

    @Bean
    @RefreshScope
    public WhiteListFilter whiteListFilter(WhiteProperties whiteProperties) {
        return new WhiteListFilter(whiteProperties);
    }

    @Bean
    public TraceFilter traceFilter() {
        return new TraceFilter();
    }

}
