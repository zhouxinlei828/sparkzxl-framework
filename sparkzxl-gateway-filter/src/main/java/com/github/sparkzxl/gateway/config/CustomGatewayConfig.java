package com.github.sparkzxl.gateway.config;

import com.github.sparkzxl.gateway.filter.BlackListFilter;
import com.github.sparkzxl.gateway.properties.BlackProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 网关配置
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties({BlackProperties.class})
public class CustomGatewayConfig {

    @Bean
    public BlackListFilter blackListFilter(BlackProperties blackProperties) {
        return new BlackListFilter(blackProperties);
    }

}
