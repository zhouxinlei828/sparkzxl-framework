package com.sparksys.cache.config;

import com.sparksys.cache.lock.RedisDistributedLock;
import com.sparksys.cache.repository.CaffeineRepositoryImpl;
import com.sparksys.cache.utils.RedisObjectSerializer;
import com.sparksys.cache.utils.TokenUtil;
import com.sparksys.core.repository.CacheRepository;
import com.sparksys.cache.repository.RedisRepositoryImpl;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * description: 缓存自动配置类
 *
 * @author: zhouxinlei
 * @date: 2020-07-09 12:05:47
 */
@Configuration
public class CacheAutoConfiguration {

    /**
     * redisTemplate设置
     *
     * @param redisConnectionFactory redis连接工厂
     * @return RedisTemplate<String, Object>
     */
    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        RedisObjectSerializer redisObjectSerializer = new RedisObjectSerializer();
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(redisObjectSerializer);
        redisTemplate.setValueSerializer(redisObjectSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    public RedisDistributedLock redisDistributedLock(RedissonClient redissonClient) {
        RedisDistributedLock redisDistributedLock = new RedisDistributedLock();
        redisDistributedLock.setRedissonClient(redissonClient);
        return redisDistributedLock;
    }

    @Bean
    public CacheRepository caffeineCacheRepository() {
        return new CaffeineRepositoryImpl();
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    @Primary
    public CacheRepository redisCacheRepository(RedisTemplate<String, Object> redisTemplate) {
        return new RedisRepositoryImpl(redisTemplate);
    }

    @Bean
    public TokenUtil tokenUtil(CacheRepository cacheRepository) {
        return new TokenUtil(cacheRepository);
    }

}
