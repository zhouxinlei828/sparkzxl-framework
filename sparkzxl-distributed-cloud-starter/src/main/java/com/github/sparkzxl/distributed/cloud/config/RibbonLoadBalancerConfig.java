package com.github.sparkzxl.distributed.cloud.config;

import com.github.sparkzxl.constant.ConfigurationConstant;
import com.github.sparkzxl.distributed.cloud.properties.LoadBalancerRuleProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;

/**
 * description: Ribbon负载均衡配置
 *
 * @author zhouxinlei
 */
@Configuration
@ConditionalOnProperty(prefix = ConfigurationConstant.RIBBON_PREFIX, name = "enabled", havingValue = "true")
@RibbonClients(defaultConfiguration = GlobalRibbonConfig.class)
@EnableConfigurationProperties(LoadBalancerRuleProperties.class)
@Slf4j
public class RibbonLoadBalancerConfig {

    public RibbonLoadBalancerConfig() {
        log.info("负载均衡配置加载");
    }
}
