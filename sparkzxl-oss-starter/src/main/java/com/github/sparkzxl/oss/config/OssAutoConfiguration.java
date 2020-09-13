package com.github.sparkzxl.oss.config;

import com.github.sparkzxl.oss.http.OssEndpoint;
import com.github.sparkzxl.oss.properties.OssProperties;
import com.github.sparkzxl.oss.service.OssTemplate;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: oss自动配置
 *
 * @author: zhouxinlei
 * @date: 2020-09-12 22:40:38
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    private final OssProperties ossProperties;

    @Bean
    @ConditionalOnMissingBean(OssTemplate.class)
    @ConditionalOnProperty(name = "sparkzxl.oss.enable", havingValue = "true", matchIfMissing = true)
    public OssTemplate ossTemplate() {
        return new OssTemplate(ossProperties);
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "sparkzxl.oss.info", havingValue = "true")
    public OssEndpoint ossEndpoint(OssTemplate template) {
        return new OssEndpoint(template);
    }

}
