package com.github.sparkzxl.jwt.config;

import com.github.sparkzxl.jwt.properties.JwtProperties;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import com.github.sparkzxl.jwt.service.impl.JwtTokenServiceImpl;
import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: jwt自动装配
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties({JwtProperties.class})
public class JwtAutoConfiguration {

    @Bean
    public JwtTokenService jwtTokenService(JwtProperties jwtProperties) {
        return new JwtTokenServiceImpl(jwtProperties, jwtProperties.getKeyStore(), Maps.newHashMap());
    }
}
