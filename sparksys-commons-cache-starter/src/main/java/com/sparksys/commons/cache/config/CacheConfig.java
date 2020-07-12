package com.sparksys.commons.cache.config;

import com.sparksys.commons.cache.components.TokenUtil;
import com.sparksys.commons.core.repository.CacheRepository;
import com.sparksys.commons.cache.repository.CaffeineRepositoryImpl;
import com.sparksys.commons.cache.repository.RedisRepositoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * description: 缓存配置类
 *
 * @author: zhouxinlei
 * @date: 2020-07-09 12:05:47
 */
@Configuration
@Order(3)
public class CacheConfig {

    @Bean
    @ConditionalOnProperty(name = "cache.redis.enable", havingValue = "false")
    public CacheRepository caffeineCacheRepository() {
        return new CaffeineRepositoryImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "cache.redis.enable", havingValue = "true")
    @Primary
    public CacheRepository redisCacheRepository(RedisTemplate<String, Object> redisTemplate) {
        return new RedisRepositoryImpl(redisTemplate);
    }

    /*@Bean
    public CacheRepository guavaCacheRepository() {
        return new GuavaCacheRepositoryImpl();
    }*/

    @Bean
    public TokenUtil tokenUtil(CacheRepository cacheRepository) {
        return new TokenUtil(cacheRepository);
    }
}
