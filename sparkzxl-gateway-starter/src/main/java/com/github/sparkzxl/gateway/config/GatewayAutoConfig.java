package com.github.sparkzxl.gateway.config;

import com.github.sparkzxl.entity.core.JwtUserInfo;
import com.github.sparkzxl.gateway.event.ApplicationLogRunner;
import com.github.sparkzxl.gateway.filter.authorization.AbstractAuthorizationFilter;
import com.github.sparkzxl.gateway.filter.log.MDCLogFilter;
import com.github.sparkzxl.gateway.properties.GatewayResourceProperties;
import com.github.sparkzxl.gateway.support.GatewayException;
import com.google.common.net.HttpHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 网关配置
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties({GatewayResourceProperties.class})
public class GatewayAutoConfig {

    private final static String DEFAULT_AUTHORIZATION_FILTER_NAME = "authorizationFilter";

    @Bean(name = DEFAULT_AUTHORIZATION_FILTER_NAME)
    @ConditionalOnMissingBean
    public GlobalFilter authorizationFilter() {
        return new AbstractAuthorizationFilter() {
            @Override
            public String getHeaderKey() {
                return HttpHeaders.AUTHORIZATION;
            }

            @Override
            public JwtUserInfo getJwtUserInfo(String token) throws GatewayException {
                return new JwtUserInfo();
            }
        };
    }

    @Bean
    public MDCLogFilter mdcLogFilter() {
        return new MDCLogFilter();
    }

    @Bean
    public ApplicationLogRunner applicationRunner(ApplicationContext applicationContext) {
        return new ApplicationLogRunner(applicationContext);
    }
}
