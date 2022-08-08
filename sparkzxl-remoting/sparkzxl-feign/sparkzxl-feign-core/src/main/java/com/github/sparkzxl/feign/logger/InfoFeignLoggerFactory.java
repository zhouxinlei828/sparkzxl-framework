package com.github.sparkzxl.feign.logger;

import org.springframework.cloud.openfeign.FeignLoggerFactory;

/**
 * @author zhouxinlei
 * @see org.springframework.cloud.openfeign.DefaultFeignLoggerFactory
 */
public class InfoFeignLoggerFactory implements FeignLoggerFactory {
    @Override
    public feign.Logger create(Class<?> type) {
        return new InfoSlf4jFeignLogger();
    }
}
