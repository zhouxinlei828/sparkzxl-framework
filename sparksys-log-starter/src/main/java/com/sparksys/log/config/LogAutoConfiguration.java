package com.sparksys.log.config;

import com.sparksys.log.aspect.WebLogAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: log 自动配置
 *
 * @author: zhouxinlei
 * @date: 2020-07-18 08:19:06
 */
@Configuration
public class LogAutoConfiguration {

    @Bean
    public WebLogAspect webLogAspect() {
        return new WebLogAspect();
    }
}
