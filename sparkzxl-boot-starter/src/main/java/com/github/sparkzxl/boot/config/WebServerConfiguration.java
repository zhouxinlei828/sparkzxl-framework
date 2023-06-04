package com.github.sparkzxl.boot.config;

import com.github.sparkzxl.boot.ApplicationLogRunner;
import com.github.sparkzxl.boot.EnableSpringUtil;
import com.github.sparkzxl.boot.undertow.DefaultWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2023-06-04 08:50:57
 */
@EnableSpringUtil
@Configuration(proxyBeanMethods = false)
public class WebServerConfiguration {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableUndertowWebServerFactory> undertowWebServerAccessLogTimingEnabler(ServerProperties serverProperties) {
        return new DefaultWebServerFactoryCustomizer(serverProperties);
    }

    @Bean
    public ApplicationLogRunner applicationRunner(ApplicationContext applicationContext) {
        return new ApplicationLogRunner(applicationContext);
    }
}
