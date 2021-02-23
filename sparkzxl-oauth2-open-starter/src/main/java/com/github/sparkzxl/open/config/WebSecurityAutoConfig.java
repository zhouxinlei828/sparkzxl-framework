package com.github.sparkzxl.open.config;

import cn.hutool.core.util.ArrayUtil;
import com.github.sparkzxl.core.resource.SwaggerStaticResource;
import com.github.sparkzxl.open.component.RestAuthenticationEntryPoint;
import com.github.sparkzxl.open.component.RestfulAccessDeniedHandler;
import com.github.sparkzxl.open.properties.SecurityProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.List;

/**
 * description: 安全认证
 *
 * @author: zhouxinlei
 * @date: 2021-02-23 14:19:05
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class WebSecurityAutoConfig extends WebSecurityConfigurerAdapter {

    @Autowired(required = false)
    private UserDetailsService userDetailsService;
    @Autowired
    private SecurityProperties securityProperties;

    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        RestfulAccessDeniedHandler restfulAccessDeniedHandler = new RestfulAccessDeniedHandler();
        RestAuthenticationEntryPoint restAuthenticationEntryPoint = new RestAuthenticationEntryPoint();
        List<String> ignorePatternList = securityProperties.getIgnorePatterns();
        if (CollectionUtils.isNotEmpty(ignorePatternList)) {
            http.authorizeRequests()
                    .antMatchers(ArrayUtil.toArray(ignorePatternList, String.class)).permitAll();
        }
        http.csrf().ignoringRequestMatchers(EndpointRequest.toAnyEndpoint());
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler);
        if (securityProperties.isRestAuthentication()) {
            http.authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and()
                    .httpBasic()
                    .and()
                    .exceptionHandling()
                    .accessDeniedHandler(restfulAccessDeniedHandler)
                    .authenticationEntryPoint(restAuthenticationEntryPoint);
        } else {
            http.authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and()
                    .httpBasic()
                    .and()
                    .exceptionHandling()
                    .accessDeniedHandler(restfulAccessDeniedHandler);
        }
    }

    @Override
    public void configure(WebSecurity web) {
        List<String> ignorePatternList = SwaggerStaticResource.EXCLUDE_STATIC_PATTERNS;
        web.ignoring().antMatchers(ArrayUtil.toArray(ignorePatternList, String.class));
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        web.httpFirewall(firewall);
    }

}
