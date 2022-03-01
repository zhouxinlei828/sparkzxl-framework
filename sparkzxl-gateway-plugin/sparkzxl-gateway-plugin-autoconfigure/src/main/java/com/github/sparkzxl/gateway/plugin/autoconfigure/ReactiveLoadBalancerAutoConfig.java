package com.github.sparkzxl.gateway.plugin.autoconfigure;

import com.github.sparkzxl.gateway.plugin.loadbalancer.GatewayLoadBalancerClientFilter;
import com.github.sparkzxl.gateway.plugin.loadbalancer.rule.RoundRobinLoadBalancerRule;
import com.github.sparkzxl.gateway.plugin.properties.ReactiveLoadBalancerProperties;
import com.github.sparkzxl.gateway.plugin.loadbalancer.rule.ILoadBalancerRule;
import com.github.sparkzxl.gateway.plugin.loadbalancer.rule.RandomLoadBalancerRule;
import com.github.sparkzxl.gateway.plugin.loadbalancer.service.IReactorServiceInstanceLoadBalancer;
import com.github.sparkzxl.gateway.plugin.loadbalancer.service.ReactorServiceInstanceLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.config.GatewayReactiveLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * description: 网关路由负载模式自动装配
 *
 * @author zhouxinlei
 */
@Configuration
@AutoConfigureBefore(GatewayReactiveLoadBalancerClientAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(prefix = ReactiveLoadBalancerProperties.GRAY_PROPERTIES_PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ReactiveLoadBalancerProperties.class})
@Slf4j
public class ReactiveLoadBalancerAutoConfig {

    @Bean
    public ILoadBalancerRule randomLoadBalancerRule(DiscoveryClient discoveryClient) {
        return new RandomLoadBalancerRule(discoveryClient);
    }

    @Bean
    public ILoadBalancerRule roundRobinLoadBalancerRule(DiscoveryClient discoveryClient) {
        return new RoundRobinLoadBalancerRule(discoveryClient);
    }

    @Bean
    public IReactorServiceInstanceLoadBalancer reactorServiceInstanceLoadBalancer(List<ILoadBalancerRule> loadBalancerRuleList, ReactiveLoadBalancerProperties reactiveLoadBalancerProperties) {
        return new ReactorServiceInstanceLoadBalancer(loadBalancerRuleList, reactiveLoadBalancerProperties);
    }

    @Bean
    @ConditionalOnMissingBean(GatewayLoadBalancerClientFilter.class)
    public ReactiveLoadBalancerClientFilter gatewayLoadBalancerClientFilter(IReactorServiceInstanceLoadBalancer reactorServiceInstanceLoadBalancer,
                                                                            @Autowired(required = false) LoadBalancerProperties properties) {
        if (ObjectUtils.isEmpty(reactorServiceInstanceLoadBalancer)) {
            throw new IllegalArgumentException("not found ReactorServiceInstanceLoadBalancer,please confirm whether it has been loaded routeLoadBalancer bean");
        }
        return new GatewayLoadBalancerClientFilter(reactorServiceInstanceLoadBalancer, properties);
    }

}
