package com.github.sparkzxl.cache.config;

import com.github.sparkzxl.cache.template.CacheCaffeineTemplateImpl;
import com.github.sparkzxl.cache.template.CacheTemplate;
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
    public CacheTemplate cacheCaffeineTemplate() {
        return new CacheCaffeineTemplateImpl();
    }

}
