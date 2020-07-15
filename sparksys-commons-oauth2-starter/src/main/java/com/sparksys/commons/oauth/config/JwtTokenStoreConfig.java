package com.sparksys.commons.oauth.config;

import com.sparksys.commons.oauth.enhancer.JwtTokenEnhancer;
import com.sparksys.commons.web.component.SpringContextUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.security.KeyPair;

/**
 * description: 使用Jwt存储token的配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:24:30
 */
@Configuration
public class JwtTokenStoreConfig {

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        KeyPair keyPair = SpringContextUtils.getBean(KeyPair.class);
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        if (ObjectUtils.isNotEmpty(keyPair)){
            jwtAccessTokenConverter.setKeyPair(keyPair);
        }
        return jwtAccessTokenConverter;
    }

    @Bean
    public JwtTokenEnhancer jwtTokenEnhancer() {
        return new JwtTokenEnhancer();
    }
}
