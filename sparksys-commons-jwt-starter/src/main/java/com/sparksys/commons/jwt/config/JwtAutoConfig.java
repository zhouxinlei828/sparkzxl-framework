package com.sparksys.commons.jwt.config;

import com.sparksys.commons.jwt.properties.JwtProperties;
import com.sparksys.commons.jwt.service.JwtTokenService;
import com.sparksys.commons.jwt.service.impl.JwtTokenServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * description: jwt自动装配
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 08:08:02
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtAutoConfig {

    @Bean
    public JwtTokenService jwtTokenService(JwtProperties jwtProperties) {
        return new JwtTokenServiceImpl(jwtProperties, keyPair(jwtProperties));
    }

    /**
     * 读取固定的公钥和私钥来进行签名和验证
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "sparksys.jwt.keyStore.enable", havingValue = "true")
    public KeyPair keyPair(JwtProperties jwtProperties) {
        //从classpath下的证书中获取秘钥对
        JwtProperties.KeyStore keyStore = jwtProperties.getKeyStore();
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(keyStore.getPath()),
                keyStore.getPassword().toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", keyStore.getPassword().toCharArray());
    }
}
