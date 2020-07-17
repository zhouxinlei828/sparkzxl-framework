package com.sparksys.zookeeper.config;

import com.sparksys.zookeeper.lock.ZkDistributedLock;
import com.sparksys.zookeeper.properties.CuratorProperties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:45:22
 */
@Configuration
@EnableConfigurationProperties(CuratorProperties.class)
public class CuratorConfig {

    @Bean(initMethod = "start")
    public CuratorFramework curatorFramework(CuratorProperties curatorProperties) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(curatorProperties.getTimeout(), curatorProperties.getRetryCount());
        return CuratorFrameworkFactory.newClient(
                curatorProperties.getUrl(),
                curatorProperties.getSessionTimeoutMs(),
                curatorProperties.getConnectionTimeoutMs(),
                retryPolicy);
    }

    @Bean
    public ZkDistributedLock distributedZkLock(CuratorFramework curatorFramework) {
        return new ZkDistributedLock(curatorFramework);
    }
}
