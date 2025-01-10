package com.github.sparkzxl.signature.client.config;

import com.github.sparkzxl.signature.client.aspect.SignMethodAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 签名自动装配
 *
 * @author zhouxinlei
 * @since 2024-05-21 14:16:40
 */
@Configuration
public class SignatureClientAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public SignMethodAspect signMethodAspect() {
        return new SignMethodAspect((key) -> null);
    }

}
