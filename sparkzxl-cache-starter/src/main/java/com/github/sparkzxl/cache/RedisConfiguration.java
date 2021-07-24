package com.github.sparkzxl.cache;

import com.github.sparkzxl.cache.redis.RedisOps;
import com.github.sparkzxl.cache.serializer.RedisObjectSerializer;
import com.github.sparkzxl.cache.template.GeneralCacheService;
import com.github.sparkzxl.cache.template.RedisCacheImpl;
import com.github.sparkzxl.cache.utils.CacheTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * description: redis缓存配置
 *
 * @author zhouxinlei
 */
@Configuration
@Slf4j
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfiguration {

    /**
     * redisTemplate设置
     *
     * @param redisConnectionFactory redis连接工厂
     * @return RedisTemplate<String, Object>
     */
    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisTemplate<String, Object> redisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Object> redisObjectSerializer = new RedisObjectSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(redisObjectSerializer);
        redisTemplate.setValueSerializer(redisObjectSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean("stringRedisTemplate")
    @ConditionalOnBean(RedisConnectionFactory.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean("redisCacheTemplate")
    @ConditionalOnBean(RedisTemplate.class)
    @Primary
    public GeneralCacheService redisCacheTemplate(RedisTemplate<String, Object> redisTemplate) {
        log.info("Autowired redisCacheTemplate success!");
        return new RedisCacheImpl(redisTemplate);
    }

    @Bean
    public CacheTokenUtils tokenUtil(GeneralCacheService generalCacheService) {
        return new CacheTokenUtils(generalCacheService);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisOps redisOps(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        return new RedisOps(redisTemplate, stringRedisTemplate, true);
    }

}
