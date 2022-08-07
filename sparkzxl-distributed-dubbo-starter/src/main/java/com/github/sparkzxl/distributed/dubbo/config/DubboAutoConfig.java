package com.github.sparkzxl.distributed.dubbo.config;

import com.github.sparkzxl.distributed.dubbo.filter.ValidationExceptionFilter;
import com.github.sparkzxl.distributed.dubbo.properties.DubboCustomProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: dubbo定制化自动装配类
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties(DubboCustomProperties.class)
public class DubboAutoConfig {

    @Bean
    public ValidationExceptionFilter dubboExceptionFilter() {
        return new ValidationExceptionFilter();
    }
}
