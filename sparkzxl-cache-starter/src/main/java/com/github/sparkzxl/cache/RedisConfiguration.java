package com.github.sparkzxl.cache;

import com.github.sparkzxl.cache.redis.RedisOps;
import com.github.sparkzxl.cache.serializer.RedisObjectSerializer;
import com.github.sparkzxl.cache.service.CacheService;
import com.github.sparkzxl.cache.service.RedisCacheImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * description: redis缓存配置
 *
 * @author zhouxinlei
 */
@Configuration
@Slf4j
@ConditionalOnClass(RedisConnectionFactory.class)
public class RedisConfiguration {

    /**
     * redisTemplate设置
     *
     * @param redisConnectionFactory redis连接工厂
     * @return RedisTemplate<String, Object>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
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

    @Bean
    @ConditionalOnMissingBean
    public RedisOps redisOps(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        return new RedisOps(redisTemplate, stringRedisTemplate, true);
    }

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    @ConditionalOnMissingBean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer messageListenerContainer = new RedisMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(redisConnectionFactory);
        return messageListenerContainer;
    }

    @Bean("redisCacheTemplate")
    @ConditionalOnBean(RedisTemplate.class)
    @Primary
    public CacheService redisCacheTemplate(RedisTemplate<String, Object> redisTemplate, RedisOps redisOps) {
        log.info("Autowired redisCacheTemplate success!");
        return new RedisCacheImpl(redisTemplate, redisOps);
    }

}
