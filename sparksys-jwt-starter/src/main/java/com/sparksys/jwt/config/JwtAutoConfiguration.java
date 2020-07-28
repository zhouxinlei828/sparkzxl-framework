package com.sparksys.jwt.config;

import com.sparksys.core.utils.KeyPairUtils;
import com.sparksys.jwt.properties.JwtProperties;
import com.sparksys.jwt.service.JwtTokenService;
import com.sparksys.jwt.service.impl.JwtTokenServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;

/**
 * description: jwt自动装配
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 08:08:02
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtAutoConfiguration {

    @Bean
    public JwtTokenService jwtTokenService(JwtProperties jwtProperties) {
        return new JwtTokenServiceImpl(jwtProperties, keyPair(jwtProperties));
    }

    /**
     * 读取固定的公钥和私钥来进行签名和验证
     *
     * @return KeyPair
     */
    @Bean
    @ConditionalOnMissingBean(KeyPair.class)
    @ConditionalOnProperty(name = "sparksys.jwt.key-store.enable", havingValue = "true")
    public KeyPair keyPair(JwtProperties jwtProperties) {
        //从classpath下的证书中获取秘钥对
        JwtProperties.KeyStore keyStore = jwtProperties.getKeyStore();
        return KeyPairUtils.keyPair(keyStore.getPath(), "jwt", keyStore.getPassword());
    }
}
