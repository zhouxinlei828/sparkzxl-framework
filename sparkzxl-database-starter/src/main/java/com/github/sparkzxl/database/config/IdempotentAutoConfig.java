package com.github.sparkzxl.database.config;

import com.github.sparkzxl.cache.template.GeneralCacheService;
import com.github.sparkzxl.database.aspect.ApiIdempotentAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: api幂等自动装配
 *
 * @author charles.zhou
 * @date 2021-05-19 14:48:42
 */
@Configuration
public class IdempotentAutoConfig {

    @Bean
    public ApiIdempotentAspect apiIdempotentAspect(GeneralCacheService generalCacheService) {
        return new ApiIdempotentAspect(generalCacheService);
    }
}
