package com.github.sparkzxl.cache.config;

import com.github.sparkzxl.cache.serializer.FastJson2JsonRedisSerializer;
import com.github.sparkzxl.cache.serializer.RedisObjectSerializer;
import com.github.sparkzxl.cache.utils.TokenUtil;
import com.github.sparkzxl.cache.template.RedisCacheTemplateImpl;
import com.github.sparkzxl.cache.template.CacheTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * description: redis缓存配置
 *
 * @author: zhouxinlei
 * @date: 2020-07-31 14:51:06
 */
@Configuration
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

    @Bean
    public FastJson2JsonRedisSerializer<Object> fastJson2JsonRedisSerializer() {
        return new FastJson2JsonRedisSerializer<>(Object.class);
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    @Primary
    public CacheTemplate redisCacheTemplate(RedisTemplate<String, Object> redisTemplate) {
        return new RedisCacheTemplateImpl(redisTemplate);
    }

    @Bean
    public TokenUtil tokenUtil(CacheTemplate cacheTemplate) {
        return new TokenUtil(cacheTemplate);
    }


}
