package com.github.sparkzxl.oss.config;

import com.github.sparkzxl.oss.properties.OssProperties;
import com.github.sparkzxl.oss.service.OssTemplate;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: oss自动配置
 *
 * @author zhouxinlei
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(OssTemplate.class)
    @ConditionalOnProperty(name = "oss.enabled", havingValue = "true")
    public OssTemplate ossTemplate(OssProperties ossProperties) {
        return new OssTemplate(() -> ossProperties);
    }

}
