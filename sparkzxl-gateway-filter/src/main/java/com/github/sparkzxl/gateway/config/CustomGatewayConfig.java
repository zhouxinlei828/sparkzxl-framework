package com.github.sparkzxl.gateway.config;

import com.github.sparkzxl.gateway.filter.WhiteListFilter;
import com.github.sparkzxl.gateway.properties.WhiteProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 网关配置
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties({WhiteProperties.class})
public class CustomGatewayConfig {

    @Bean
    public WhiteListFilter whiteListFilter(WhiteProperties whiteProperties) {
        return new WhiteListFilter(whiteProperties);
    }

}
