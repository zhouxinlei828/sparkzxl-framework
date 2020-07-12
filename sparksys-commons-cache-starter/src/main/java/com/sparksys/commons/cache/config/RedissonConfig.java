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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * description：Redisson配置
 *
 * @author zhouxinlei
 * @date 2020/6/10 0010
 */
@Configuration
@ConditionalOnClass(Config.class)
@ConditionalOnProperty(name = "cache.redisson.enable", havingValue = "true")
@Order(2)
public class RedissonConfig {


    @Autowired
    private CacheProperties cacheProperties;


    @Bean
    public RedissonClient redissonClient() {
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
