package com.github.sparkzxl.database;

import com.github.sparkzxl.cache.template.GeneralCacheService;
import com.github.sparkzxl.database.aspect.ApiIdempotentAspect;
import com.github.sparkzxl.database.aspect.LockKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: api幂等自动装配
 *
 * @author charles.zhou
 */
@Configuration
public class IdempotentAutoConfig {

    @Bean
    public ApiIdempotentAspect apiIdempotentAspect(@Autowired GeneralCacheService generalCacheService) {
        ApiIdempotentAspect apiIdempotentAspect = new ApiIdempotentAspect(generalCacheService);
        LockKeyGenerator lockKeyGenerator = new LockKeyGenerator();
        apiIdempotentAspect.setLockKeyGenerator(lockKeyGenerator);
        return apiIdempotentAspect;
    }
}
