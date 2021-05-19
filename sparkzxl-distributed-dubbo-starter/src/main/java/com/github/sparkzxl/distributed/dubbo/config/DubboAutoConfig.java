package com.github.sparkzxl.distributed.dubbo.config;

import com.github.sparkzxl.distributed.dubbo.filter.DubboExceptionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: dubbo定制化自动装配类
 *
 * @author zhouxinlei
 */
@Configuration
public class DubboAutoConfig {

    @Bean
    public DubboExceptionFilter dubboExceptionFilter() {
        return new DubboExceptionFilter();
    }
}
