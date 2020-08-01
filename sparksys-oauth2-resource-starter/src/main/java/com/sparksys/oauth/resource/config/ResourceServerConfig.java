package com.sparksys.oauth.resource.config;

import com.sparksys.oauth.resource.component.RestAuthenticationEntryPoint;
import com.sparksys.oauth.resource.component.RestfulAccessDeniedHandler;
import com.sparksys.oauth.resource.properties.ResourceProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * description: 资源保护配置类
 *
 * @author: zhouxinlei
 * @date: 2020-08-01 13:49:22
 */
@Configuration
@AllArgsConstructor
@EnableWebFluxSecurity
@EnableConfigurationProperties(ResourceProperties.class)
public class ResourceServerConfig {

    private final ResourceProperties resourceProperties;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
        http.authorizeExchange()
                //白名单配置
                .pathMatchers(resourceProperties.getIgnorePatterns()).permitAll()
                .and().authorizeExchange().pathMatchers(resourceProperties.getProtectPatterns()).authenticated()
                .and().exceptionHandling()
                //处理未授权
                .authenticationEntryPoint(restAuthenticationEntryPoint())
                //处理未认证
                .accessDeniedHandler(restfulAccessDeniedHandler())
                .and().csrf().disable();
        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public RestfulAccessDeniedHandler restfulAccessDeniedHandler() {
        return new RestfulAccessDeniedHandler();
    }

}
