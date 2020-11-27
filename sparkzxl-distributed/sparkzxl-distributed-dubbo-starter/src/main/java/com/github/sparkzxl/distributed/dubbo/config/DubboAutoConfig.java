package com.github.sparkzxl.distributed.dubbo.config;

import com.github.sparkzxl.distributed.dubbo.filter.DubboExceptionFilter;
import com.github.sparkzxl.distributed.dubbo.support.SentinelExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * description: dubbo定制化自动装配类
 *
 * @author: zhouxinlei
 * @date: 2020-11-27 10:30:15
 */
@Configuration
@Import({SentinelExceptionHandler.class})
public class DubboAutoConfig {

    @Bean
    public DubboExceptionFilter dubboExceptionFilter(){
        return new DubboExceptionFilter();
    }
}
