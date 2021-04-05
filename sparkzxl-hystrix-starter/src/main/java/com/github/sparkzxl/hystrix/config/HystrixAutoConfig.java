package com.github.sparkzxl.hystrix.config;

import com.github.sparkzxl.hystrix.strategy.ThreadLocalHystrixConcurrencyStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: Hystrix自动装配
 *
 * @author zhouxinlei
 */
@Configuration
public class HystrixAutoConfig {

    /**
     * 本地线程 Hystrix并发策略
     *
     * @return ThreadLocalHystrixConcurrencyStrategy
     */
    @Bean
    public ThreadLocalHystrixConcurrencyStrategy threadLocalHystrixConcurrencyStrategy() {
        return new ThreadLocalHystrixConcurrencyStrategy();
    }

}
