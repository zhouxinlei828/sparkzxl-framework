package com.github.sparkzxl.distributed.cloud.config;

import cn.hutool.core.date.DateUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-03-19 23:02:53
 */
@Configuration
public class NacosConfig {

    @Bean
    @ConditionalOnProperty(value = {"spring.cloud.nacos.discovery.watch.enabled"}, matchIfMissing = true)
    public NacosWatch nacosWatch(NacosServiceManager nacosServiceManager, NacosDiscoveryProperties nacosDiscoveryProperties, ObjectProvider<ThreadPoolTaskScheduler> taskScheduler) {
        //更改服务详情中的元数据，增加服务注册时间
        nacosDiscoveryProperties.getMetadata().put("startup.time", DateUtil.now());
        return new NacosWatch(nacosServiceManager, nacosDiscoveryProperties, taskScheduler);
    }

}
