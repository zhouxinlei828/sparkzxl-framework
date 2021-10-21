package com.github.sparkzxl.distributed.cloud.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.github.sparkzxl.core.utils.DateUtils;
import com.github.sparkzxl.distributed.cloud.event.CloudApplicationInitRunner;
import com.github.sparkzxl.distributed.cloud.loadbalancer.PreferredVersionRule;
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
public class CloudAutoConfig {

    @Bean
    public CloudApplicationInitRunner cloudApplicationInitRunner() {
        return new CloudApplicationInitRunner();
    }

    @Bean
    public PreferredVersionRule preferredVersionRule() {
        return new PreferredVersionRule();
    }

    @Bean
    public NacosDiscoveryProperties nacosProperties() {
        NacosDiscoveryProperties nacosDiscoveryProperties = new NacosDiscoveryProperties();
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        metadata.put("startup.time", DateUtils.formatDateTime(new Date()));
        return nacosDiscoveryProperties;
    }
}
