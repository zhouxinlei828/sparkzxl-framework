package com.github.sparkzxl.log.config;

import com.github.sparkzxl.log.aspect.WebLogAspect;
import com.github.sparkzxl.log.properties.LogProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: log 自动配置
 *
 * @author: zhouxinlei
 * @date: 2020-07-18 08:19:06
 */
@Configuration
@EnableConfigurationProperties(value = LogProperties.class)
public class LogAutoConfiguration {

    @Bean
    public WebLogAspect webLogAspect() {
        return new WebLogAspect();
    }

}
