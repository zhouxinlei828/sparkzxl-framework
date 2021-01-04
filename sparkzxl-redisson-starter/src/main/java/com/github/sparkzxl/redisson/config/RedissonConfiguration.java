package com.github.sparkzxl.redisson.config;

import com.github.sparkzxl.redisson.aspect.RedisLockAspect;
import com.github.sparkzxl.redisson.lock.RedisDistributedLock;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: redis缓存配置
 *
 * @author: zhouxinlei
 * @date: 2020-07-31 14:51:06
 */
@Configuration
@AutoConfigureAfter(RedissonAutoConfiguration.class)
public class RedissonConfiguration {

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    public RedisDistributedLock redisDistributedLock(RedissonClient redissonClient) {
        RedisDistributedLock redisDistributedLock = new RedisDistributedLock();
        redisDistributedLock.setRedissonClient(redissonClient);
        return redisDistributedLock;
    }

    @Bean
    public RedisLockAspect redisLockAspect() {
        return new RedisLockAspect();
    }
}
