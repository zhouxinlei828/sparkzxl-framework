package com.github.sparkzxl.feign.resilience4j.autoconfigure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * description: feign自动配置
 *
 * @author zhouxinlei
 * @since 2022-04-04 14:56:18
 */
@Configuration(proxyBeanMethods = false)
@Import(Resilience4jFeignAutoConfig.class)
public class OpenFeignAutoConfiguration {
}
