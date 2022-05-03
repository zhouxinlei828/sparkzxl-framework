package com.github.sparkzxl.lock.spring.boot.autoconfigure;

import com.github.sparkzxl.lock.condition.ZookeeperCondition;
import com.github.sparkzxl.lock.executor.ZookeeperLockExecutor;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * description: Zookeeper锁自动配置器
 *
 * @author zhouxinlei
 * @since 2022-05-03 10:08:06
 */
@Conditional(ZookeeperCondition.class)
@Configuration
@EnableConfigurationProperties(CuratorProperties.class)
class ZookeeperLockAutoConfiguration {

    @Bean(initMethod = "start", destroyMethod = "close")
    @ConditionalOnMissingBean(CuratorFramework.class)
    public CuratorFramework curatorFramework(CuratorProperties curatorProperties) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(curatorProperties.getTimeout(), curatorProperties.getRetryCount());
        return CuratorFrameworkFactory.newClient(
                curatorProperties.getZkServers(),
                curatorProperties.getSessionTimeoutMs(),
                curatorProperties.getConnectionTimeoutMs(),
                retryPolicy);
    }

    @Bean
    @ConditionalOnBean(CuratorFramework.class)
    @Order(300)
    public ZookeeperLockExecutor zookeeperLockExecutor(CuratorFramework curatorFramework) {
        return new ZookeeperLockExecutor(curatorFramework);
    }
}