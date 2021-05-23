package com.github.sparkzxl.log.config;

import com.github.sparkzxl.log.aspect.WebLogAspect;
import com.github.sparkzxl.log.properties.LogProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 日志增强自动装配
 *
 * @author zhouxinlei
 * @date 2021-05-23 13:07:55
 */
@Configuration
@EnableConfigurationProperties(value = {LogProperties.class})
public class LogAutoConfiguration {

    @Bean
    public WebLogAspect webLogAspect() {
        return new WebLogAspect();
    }

}
