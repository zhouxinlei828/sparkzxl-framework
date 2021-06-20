package com.github.sparkzxl.cache;

import com.github.sparkzxl.cache.template.CaffeineCacheImpl;
import com.github.sparkzxl.cache.template.GeneralCacheService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 缓存自动配置类
 *
 * @author zhouxinlei
 */
@Configuration
public class CacheAutoConfiguration {

    @Bean
    public GeneralCacheService cacheCaffeineTemplate() {
        return new CaffeineCacheImpl();
    }

}
