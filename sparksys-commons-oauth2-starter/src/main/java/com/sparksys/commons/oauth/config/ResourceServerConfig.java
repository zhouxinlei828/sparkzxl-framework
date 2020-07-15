package com.sparksys.commons.oauth.config;

import com.sparksys.commons.oauth.rest.OauthRestAuthenticationEntryPoint;
import com.sparksys.commons.oauth.rest.OauthRestfulAccessDeniedHandler;
import com.sparksys.commons.oauth.properties.Oauth2Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import javax.annotation.Resource;

/**
 * description: 资源服务器配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:24:41
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Resource
    private Oauth2Properties oauth2Properties;


    @Bean
    public OauthRestfulAccessDeniedHandler oauthRestfulAccessDeniedHandler(){
        return new OauthRestfulAccessDeniedHandler();
    }

    @Bean
    public OauthRestAuthenticationEntryPoint oauthRestAuthenticationEntryPoint(){
        return new OauthRestAuthenticationEntryPoint();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .requestMatchers()
                //配置需要保护的资源路径
                .antMatchers(oauth2Properties.getProtectPatterns())
                .and()
                .csrf()
                .disable();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.authenticationEntryPoint(oauthRestAuthenticationEntryPoint())
                .accessDeniedHandler(oauthRestfulAccessDeniedHandler());
    }

}
