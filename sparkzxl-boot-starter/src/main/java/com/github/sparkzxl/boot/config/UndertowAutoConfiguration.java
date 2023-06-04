package com.github.sparkzxl.boot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(WebServerConfiguration.class)
public class UndertowAutoConfiguration {
}
