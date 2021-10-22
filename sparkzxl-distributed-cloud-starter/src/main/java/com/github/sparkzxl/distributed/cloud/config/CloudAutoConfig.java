package com.github.sparkzxl.distributed.cloud.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.github.sparkzxl.core.utils.DateUtils;
import com.github.sparkzxl.distributed.cloud.loadbalancer.PreferredVersionIsolationRule;
import com.github.sparkzxl.distributed.cloud.properties.LoadBalancerRuleProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.Map;

/**
 * description: cloud application 配置
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties(LoadBalancerRuleProperties.class)
public class CloudAutoConfig {

    /**
     * 版本隔离优先选择负载均衡规则
     *
     * @return PreferredVersionRule
     */
    @Bean
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
