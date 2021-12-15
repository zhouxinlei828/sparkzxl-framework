package com.github.sparkzxl.idempotent.config;

import com.github.sparkzxl.core.generator.CacheKeyGenerator;
import com.github.sparkzxl.idempotent.aspect.ApiIdempotentAspect;
import com.github.sparkzxl.idempotent.constant.IdempotentConstant;
import com.github.sparkzxl.idempotent.support.IdempotentInfoParser;
import com.github.sparkzxl.idempotent.support.IdempotentManager;
import com.github.sparkzxl.idempotent.support.LockKeyGenerator;
import com.github.sparkzxl.idempotent.support.RedisIdempotentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties(IdempotentProperties.class)
@ConditionalOnProperty(prefix = "idempotent", name = "enabled", havingValue = "true")
public class IdempotentAutoConfig {

    @Autowired
    private Map<String, CacheKeyGenerator> keyGeneratorMap;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Bean
    public IdempotentInfoParser idempotentInfoParser() {
        return new IdempotentInfoParser(keyGeneratorMap);
    }

    @Bean
    public IdempotentManager redisIdempotentManager() {
        return new RedisIdempotentManager(redisTemplate);
    }

    @Bean(name = IdempotentConstant.DEFAULT_LOCK_KEY_GENERATOR_NAME)
    public CacheKeyGenerator lockKeyGenerator() {
        return new LockKeyGenerator();
    }

    @Bean
    public ApiIdempotentAspect apiIdempotentAspect(IdempotentManager redisIdempotentManager, IdempotentInfoParser idempotentInfoParser) {
        return new ApiIdempotentAspect(redisIdempotentManager, idempotentInfoParser);
    }

}
