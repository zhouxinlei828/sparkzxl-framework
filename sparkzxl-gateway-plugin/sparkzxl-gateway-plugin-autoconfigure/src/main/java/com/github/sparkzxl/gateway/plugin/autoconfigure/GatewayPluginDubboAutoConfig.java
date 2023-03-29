package com.github.sparkzxl.gateway.plugin.autoconfigure;

import com.github.sparkzxl.gateway.plugin.dubbo.ApacheDubboFilter;
import com.github.sparkzxl.gateway.plugin.dubbo.ApacheDubboProxyService;
import com.github.sparkzxl.gateway.plugin.dubbo.RpcParamTransformFilter;
import com.github.sparkzxl.gateway.plugin.dubbo.handler.ApacheDubboFilterDataHandler;
import com.github.sparkzxl.gateway.plugin.dubbo.message.DubboMessageWriter;
import com.github.sparkzxl.gateway.plugin.dubbo.message.JacksonDubboMessageWriter;
import com.github.sparkzxl.gateway.plugin.dubbo.param.DubboParamResolveService;
import com.github.sparkzxl.gateway.plugin.dubbo.param.DubboParamResolveServiceImpl;
import com.github.sparkzxl.gateway.plugin.dubbo.route.DefaultDubboMetaDataFactory;
import com.github.sparkzxl.gateway.plugin.dubbo.route.DubboMetaDataFactory;
import com.github.sparkzxl.gateway.plugin.dubbo.route.DubboRoutePredicate;
import com.github.sparkzxl.gateway.plugin.dubbo.route.DubboRouteSchemePredicate;
import com.github.sparkzxl.gateway.properties.GatewayPluginProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: gateway plugin configuration
 *
 * @author zhouxinlei
 * @since 2022-01-08 21:18:48
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.cloud.gateway.plugin.filter.dubbo", value = "enabled", havingValue = "true")
@EnableConfigurationProperties(GatewayPluginProperties.class)
public class GatewayPluginDubboAutoConfig {

    @Bean
    public DubboMetaDataFactory dubboMetaDataFactory() {
        return new DefaultDubboMetaDataFactory();
    }

    @Bean
    public DubboRoutePredicate dubboRoutePredicate() {
        return new DubboRouteSchemePredicate();
    }

    @Bean
    public DubboParamResolveService dubboParamResolveService() {
        return new DubboParamResolveServiceImpl();
    }

    @Bean
    public ApacheDubboProxyService apacheDubboProxyService(DubboParamResolveService dubboParamResolveService) {
        return new ApacheDubboProxyService(dubboParamResolveService);
    }

    @Bean
    public DubboMessageWriter dubboMessageWriter() {
        return new JacksonDubboMessageWriter();
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(ApacheDubboFilter.class)
    public GlobalFilter apacheDubboFilter(DubboMetaDataFactory dubboMetaDataFactory,
            DubboRoutePredicate dubboRoutePredicate,
            ApacheDubboProxyService apacheDubboProxyService,
            DubboMessageWriter dubboMessageWriter) {
        return new ApacheDubboFilter(dubboMetaDataFactory, dubboRoutePredicate, apacheDubboProxyService, dubboMessageWriter);
    }

    @Bean
    @ConditionalOnMissingBean(RpcParamTransformFilter.class)
    public GlobalFilter rpcParamTransformFilter() {
        return new RpcParamTransformFilter();
    }

    @Bean
    public ApacheDubboFilterDataHandler apacheDubboFilterDataHandler() {
        return new ApacheDubboFilterDataHandler();
    }
}
