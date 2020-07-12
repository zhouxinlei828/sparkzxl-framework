package com.sparksys.commons.oauth.config;

import com.sparksys.commons.oauth.component.OauthRestAuthenticationEntryPoint;
import com.sparksys.commons.oauth.component.OauthRestfulAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * description: 资源服务器配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:24:41
 */
@Configuration
@EnableResourceServer
@Order(6)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private OauthRestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private OauthRestfulAccessDeniedHandler oauthRestfulAccessDeniedHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .requestMatchers()
                //配置需要保护的资源路径
                .antMatchers("/**")
                .and()
                .csrf()
                .disable();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(oauthRestfulAccessDeniedHandler);
    }
}
