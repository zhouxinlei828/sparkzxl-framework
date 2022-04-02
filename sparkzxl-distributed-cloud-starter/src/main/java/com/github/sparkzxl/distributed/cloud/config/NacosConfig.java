package com.github.sparkzxl.distributed.cloud.config;

import cn.hutool.core.date.DateUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-03-19 23:02:53
 */
@Configuration
public class NacosConfig {

    @Bean
    public NacosDiscoveryProperties nacosProperties() {
        NacosDiscoveryProperties nacosDiscoveryProperties = new NacosDiscoveryProperties();
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        metadata.put("startup.time", DateUtil.now());
        return nacosDiscoveryProperties;
    }

}
