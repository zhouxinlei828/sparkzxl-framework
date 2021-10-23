package com.github.sparkzxl.gateway.config;

import com.github.sparkzxl.gateway.filter.GatewayLoadBalancerClientFilter;
import com.github.sparkzxl.gateway.properties.GrayProperties;
import com.github.sparkzxl.gateway.rule.RouteLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GatewayReactiveLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 网关路由负载模式自动装配
 *
 * @author zhouxinlei
 * @date 2021-10-22 22:00:19
 */
@Configuration
@AutoConfigureBefore(GatewayReactiveLoadBalancerClientAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(prefix = GrayProperties.GRAY_PROPERTIES_PREFIX, value = "enable", havingValue = "true")
@EnableConfigurationProperties({GrayProperties.class})
@Slf4j
public class RouteLoadBalancerClientConfig {

    @Bean
    public ReactiveLoadBalancerClientFilter gatewayLoadBalancerClientFilter(@Autowired(required = false) RouteLoadBalancer grayLoadBalancer,
                                                                            @Autowired(required = false) LoadBalancerProperties properties) {
        if (ObjectUtils.isEmpty(grayLoadBalancer)) {
            throw new IllegalArgumentException("not found RouteLoadBalancer，please confirm whether it has been loaded routeLoadBalancer bean");
        }
        return new GatewayLoadBalancerClientFilter(grayLoadBalancer, properties);
    }

}
