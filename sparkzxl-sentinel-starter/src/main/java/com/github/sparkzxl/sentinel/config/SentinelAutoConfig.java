package com.github.sparkzxl.sentinel.config;

import com.github.sparkzxl.sentinel.support.CustomBlockExceptionHandler;
import com.github.sparkzxl.sentinel.support.SentinelExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: sentinel定制化自动装配类
 *
 * @author zhouxinlei
 */
@Configuration
public class SentinelAutoConfig {

    @Bean
    public CustomBlockExceptionHandler customBlockExceptionHandler() {
        return new CustomBlockExceptionHandler();
    }

    @Bean
    public SentinelExceptionHandler sentinelExceptionHandler() {
        return new SentinelExceptionHandler();
    }
}
