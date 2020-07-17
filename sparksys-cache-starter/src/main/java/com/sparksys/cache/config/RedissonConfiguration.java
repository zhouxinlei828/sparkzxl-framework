package com.sparksys.cache.config;

import cn.hutool.json.JSONUtil;
import com.sparksys.cache.lock.RedisDistributedLock;
import com.sparksys.cache.properties.CacheRedissonProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

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
@EnableConfigurationProperties(CacheRedissonProperties.class)
@Slf4j
public class RedissonConfiguration {

    @Bean
    public RedissonClient redissonClient(CacheRedissonProperties cacheProperties) {
        log.info("automatic injection RedissonClient CacheRedissonProperties：{}", JSONUtil.toJsonPrettyStr(cacheProperties));
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(cacheProperties.getAddress())
                .setTimeout(cacheProperties.getTimeout())
                .setConnectionPoolSize(cacheProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(cacheProperties.getConnectionMinimumIdleSize());
        if (StringUtils.isNotBlank(cacheProperties.getPassword())) {
            serverConfig.setPassword(cacheProperties.getPassword());
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
