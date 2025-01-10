package com.github.sparkzxl.signature.server.config;

import com.github.sparkzxl.signature.server.filter.SignAuthFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import static com.github.sparkzxl.signature.server.properties.SignatureServerProperties.CONFIG_PREFIX;

/**
 * description: WebfluxConfig
 *
 * @author zhouxinlei
 * @since 2024-07-02 17:22:01
 */
@Order(1)
@Configuration
@ConditionalOnMissingClass("org.springframework.web.servlet.DispatcherServlet")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class WebfluxConfig {

    @Bean(name = "signAuthFilter")
    @ConditionalOnProperty(prefix = CONFIG_PREFIX, name = "enabled", havingValue = "true")
    public GlobalFilter signAuthFilter() {
        return new SignAuthFilter();
    }
}
