package com.github.sparkzxl.lock.spring.boot.autoconfigure;

import com.github.sparkzxl.lock.executor.RedissonLockExecutor;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Redisson锁自动配置器
 *
 * @author zengzhihong
 */
@Configuration
@AutoConfigureAfter(RedissonAutoConfiguration.class)
public class RedissonLockAutoConfiguration {

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    @Order(100)
    public RedissonLockExecutor redissonLockExecutor(RedissonClient redissonClient) {
        return new RedissonLockExecutor(redissonClient);
    }

}
