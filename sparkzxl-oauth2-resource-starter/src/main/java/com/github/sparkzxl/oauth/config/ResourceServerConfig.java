package com.github.sparkzxl.oauth.config;

import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.resource.SwaggerStaticResource;
import com.github.sparkzxl.oauth.component.RestAuthenticationEntryPoint;
import com.github.sparkzxl.oauth.component.RestfulAccessDeniedHandler;
import com.github.sparkzxl.oauth.filter.IgnoreUrlsRemoveJwtFilter;
import com.github.sparkzxl.oauth.properties.ResourceProperties;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

/**
 * description: 资源保护配置类
 *
 * @author zhouxinlei
 */
@Configuration
@AllArgsConstructor
@EnableWebFluxSecurity
@EnableConfigurationProperties(ResourceProperties.class)
public class ResourceServerConfig {

    private final ResourceProperties resourceProperties;

    private final ReactiveAuthorizationManager<AuthorizationContext> reactiveAuthorizationManager;

    @Bean
    public IgnoreUrlsRemoveJwtFilter ignoreUrlsRemoveJwtFilter() {
        return new IgnoreUrlsRemoveJwtFilter(resourceProperties);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        String[] ignorePatterns = ArrayUtils.addAll(resourceProperties.getIgnorePatterns(),
                SwaggerStaticResource.EXCLUDE_STATIC_PATTERNS.toArray(new String[0]));
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
        //自定义处理JWT请求头过期或签名错误的结果
        RestAuthenticationEntryPoint restAuthenticationEntryPoint = new RestAuthenticationEntryPoint();
        RestfulAccessDeniedHandler restfulAccessDeniedHandler = new RestfulAccessDeniedHandler();
        http.oauth2ResourceServer().authenticationEntryPoint(restAuthenticationEntryPoint);
        //对白名单路径，直接移除JWT请求头
        http.addFilterBefore(ignoreUrlsRemoveJwtFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

        http.authorizeExchange()
                //白名单配置
                .pathMatchers(ignorePatterns).permitAll()
                .anyExchange().access(reactiveAuthorizationManager)
                .and().exceptionHandling()
                //处理未授权
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                //处理未认证
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .and().csrf().disable();
        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(BaseContextConstants.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(BaseContextConstants.AUTHORITY_CLAIM_NAME);
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
