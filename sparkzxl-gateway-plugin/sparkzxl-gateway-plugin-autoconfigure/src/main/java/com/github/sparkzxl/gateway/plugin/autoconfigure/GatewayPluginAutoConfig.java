package com.github.sparkzxl.gateway.plugin.autoconfigure;

import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.gateway.plugin.TraceFilter;
import com.github.sparkzxl.gateway.plugin.annotation.EnableExceptionJsonHandler;
import com.github.sparkzxl.gateway.plugin.filter.GatewayContextFilter;
import com.github.sparkzxl.gateway.plugin.filter.MDCFilter;
import com.github.sparkzxl.gateway.plugin.jwt.JwtFilter;
import com.github.sparkzxl.gateway.plugin.properties.GatewayPluginProperties;
import com.github.sparkzxl.gateway.plugin.resolver.ForwardedRemoteAddressResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ipresolver.RemoteAddressResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * description: gateway plugin configuration
 *
 * @author zhouxinlei
 * @since 2022-01-08 21:18:48
 */
@Configuration
@EnableConfigurationProperties(GatewayPluginProperties.class)
@Import(SpringContextUtils.class)
@EnableExceptionJsonHandler
public class GatewayPluginAutoConfig {

    @Bean(name = "mdcFilter")
    public GlobalFilter mdcFilter() {
        return new MDCFilter();
    }

    @Bean
    @ConditionalOnMissingBean(RemoteAddressResolver.class)
    public RemoteAddressResolver remoteAddressResolver() {
        return new ForwardedRemoteAddressResolver(1);
    }

    @Bean
    @ConditionalOnMissingBean(GatewayContextFilter.class)
    public GlobalFilter gatewayContextFilter() {
        return new GatewayContextFilter();
    }

    @Bean
    @ConditionalOnMissingBean(JwtFilter.class)
    @ConditionalOnProperty(prefix = "spring.cloud.gateway.plugin.filter.jwt", value = "enabled", havingValue = "true")
    public GlobalFilter jwtFilter() {
        return new JwtFilter();
    }

    @Bean
    @ConditionalOnMissingBean(TraceFilter.class)
    @ConditionalOnProperty(prefix = "spring.cloud.gateway.plugin.filter.trace", value = "enabled", havingValue = "true")
    public GlobalFilter traceFilter() {
        return new TraceFilter();
    }

}
