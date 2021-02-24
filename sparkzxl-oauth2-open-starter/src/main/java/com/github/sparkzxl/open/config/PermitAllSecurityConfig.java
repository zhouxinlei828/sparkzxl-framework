package com.github.sparkzxl.open.config;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;

import javax.servlet.Filter;

/**
 * description: 配置PermitAuthenticationFilter
 *
 * @author: zhouxinlei
 * @date: 2021-02-24 10:25:44
 */
public class PermitAllSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private Filter permitAuthenticationFilter;


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(permitAuthenticationFilter, OAuth2AuthenticationProcessingFilter.class);
    }

    public Filter getPermitAuthenticationFilter() {
        return permitAuthenticationFilter;
    }

    public void setPermitAuthenticationFilter(Filter permitAuthenticationFilter) {
        this.permitAuthenticationFilter = permitAuthenticationFilter;
    }
}
