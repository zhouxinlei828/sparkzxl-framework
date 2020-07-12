package com.sparksys.commons.cache.config;

import com.sparksys.commons.cache.lock.RedisDistributedLock;
import com.sparksys.commons.cache.properties.CacheProperties;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description：Redisson配置
 *
 * @author zhouxinlei
 * @date 2020/6/10 0010
 */
@Configuration
@ConditionalOnClass(Config.class)
@ConditionalOnProperty(name = "cache.redisson.enable", havingValue = "true")
@EnableConfigurationProperties(CacheProperties.class)
public class RedissonConfig {


    @Bean
    public RedissonClient redissonClient(CacheProperties cacheProperties) {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(cacheProperties.getRedisson().getAddress())
                .setTimeout(cacheProperties.getRedisson().getTimeout())
                .setConnectionPoolSize(cacheProperties.getRedisson().getConnectionPoolSize())
                .setConnectionMinimumIdleSize(cacheProperties.getRedisson().getConnectionMinimumIdleSize());
        if (StringUtils.isNotBlank(cacheProperties.getRedisson().getPassword())) {
            serverConfig.setPassword(cacheProperties.getRedisson().getPassword());
        }
        return Redisson.create(config);
    }

    @Bean
    RedisDistributedLock redisDistributedLock(RedissonClient redissonClient) {
        RedisDistributedLock redisDistributedLock = new RedisDistributedLock();
        redisDistributedLock.setRedissonClient(redissonClient);
        return redisDistributedLock;
    }

}
