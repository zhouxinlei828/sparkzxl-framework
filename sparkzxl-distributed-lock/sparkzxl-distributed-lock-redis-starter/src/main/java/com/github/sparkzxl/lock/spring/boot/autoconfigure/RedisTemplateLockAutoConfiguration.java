package com.github.sparkzxl.lock.spring.boot.autoconfigure;

import com.github.sparkzxl.lock.executor.RedisTemplateLockExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * description: RedisTemplate锁自动配置器
 *
 * @author zhouxinlei
 * @since 2022-05-01 22:22:30
 */
@Configuration
@ConditionalOnClass(RedisOperations.class)
public class RedisTemplateLockAutoConfiguration {

    @Bean
    @Order(200)
    public RedisTemplateLockExecutor redisTemplateLockExecutor(StringRedisTemplate stringRedisTemplate) {
        return new RedisTemplateLockExecutor(stringRedisTemplate);
    }
}