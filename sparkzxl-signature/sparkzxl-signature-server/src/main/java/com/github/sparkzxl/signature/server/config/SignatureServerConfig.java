package com.github.sparkzxl.signature.server.config;

import com.github.sparkzxl.signature.server.cache.SignCache;
import com.github.sparkzxl.signature.server.cache.SignLocalCache;
import com.github.sparkzxl.signature.server.method.SignAuthProcessor;
import com.github.sparkzxl.signature.server.method.SignProcessor;
import com.github.sparkzxl.signature.server.properties.SignatureServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 签名自动装配
 *
 * @author zhouxinlei
 * @since 2024-05-21 14:16:40
 */
@Configuration
@EnableConfigurationProperties(SignatureServerProperties.class)
public class SignatureServerConfig {

    @Bean(name = "signCache")
    @ConditionalOnMissingBean(name = "signCache")
    public SignCache signCache() {
        return new SignLocalCache();
    }

    @Bean
    @ConditionalOnMissingBean
    public SignProcessor signProcessor() {
        return new SignAuthProcessor();
    }

}
