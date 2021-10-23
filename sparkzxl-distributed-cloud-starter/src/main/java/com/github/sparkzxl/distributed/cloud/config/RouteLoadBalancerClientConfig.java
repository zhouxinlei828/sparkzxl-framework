package com.github.sparkzxl.distributed.cloud.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.github.sparkzxl.core.utils.DateUtils;
import com.github.sparkzxl.distributed.cloud.loadbalancer.PreferredVersionIsolationRule;
import com.github.sparkzxl.distributed.cloud.properties.LoadBalancerRuleProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.Map;

/**
 * description: 负载模式自动装配
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties(LoadBalancerRuleProperties.class)
public class RouteLoadBalancerClientConfig {

    /**
     * 版本隔离优先选择负载均衡规则
     *
     * @return PreferredVersionRule
     */
    @Bean
    @RefreshScope
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnProperty(name = "ribbon.isolation.enabled", havingValue = "true")
    public PreferredVersionIsolationRule preferredVersionIsolationRule() {
        return new PreferredVersionIsolationRule();
    }

    @Bean
    public NacosDiscoveryProperties nacosProperties() {
        NacosDiscoveryProperties nacosDiscoveryProperties = new NacosDiscoveryProperties();
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        metadata.put("startup.time", DateUtils.formatDateTime(new Date()));
        return nacosDiscoveryProperties;
    }
}
