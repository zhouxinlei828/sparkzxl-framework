package com.github.sparkzxl.gateway;

import com.github.sparkzxl.entity.core.JwtUserInfo;
import com.github.sparkzxl.gateway.filter.AbstractAuthorizationFilter;
import com.github.sparkzxl.gateway.properties.ResourceProperties;
import com.github.sparkzxl.gateway.support.GatewayException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 网关配置
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties({ResourceProperties.class})
public class GatewayAutoConfig {

    private final static String DEFAULT_AUTHORIZATION_FILTER_NAME = "authorizationFilter";

    @Bean(name = DEFAULT_AUTHORIZATION_FILTER_NAME)
    @ConditionalOnMissingBean
    public GlobalFilter authorizationFilter() {
        return new AbstractAuthorizationFilter() {
            @Override
            public String getHeaderKey() {
                return "";
            }

            @Override
            public JwtUserInfo getJwtUserInfo(String token) throws GatewayException {
                return new JwtUserInfo();
            }
        };
    }
}
