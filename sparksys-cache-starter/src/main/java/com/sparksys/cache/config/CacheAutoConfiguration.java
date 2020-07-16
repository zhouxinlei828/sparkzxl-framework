package com.sparksys.cache.config;

import com.sparksys.cache.repository.CaffeineRepositoryImpl;
import com.sparksys.cache.utils.TokenUtil;
import com.sparksys.core.repository.CacheRepository;
import com.sparksys.cache.repository.RedisRepositoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * description: 缓存配置类
 *
 * @author: zhouxinlei
 * @date: 2020-07-09 12:05:47
 */
@Configuration
public class CacheAutoConfiguration {

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

    @Bean
    public TokenUtil tokenUtil(CacheRepository cacheRepository) {
        return new TokenUtil(cacheRepository);
    }
}
