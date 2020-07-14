package com.sparksys.commons.jwt.config;

import com.sparksys.commons.jwt.config.properties.JwtProperties;
import com.sparksys.commons.jwt.config.service.JwtTokenService;
import com.sparksys.commons.jwt.config.service.impl.JwtTokenServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        return new JwtTokenServiceImpl(jwtProperties);
    }
}
