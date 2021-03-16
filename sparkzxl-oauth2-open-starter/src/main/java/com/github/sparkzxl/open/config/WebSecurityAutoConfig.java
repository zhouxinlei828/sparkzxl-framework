package com.github.sparkzxl.open.config;

import cn.hutool.core.util.ArrayUtil;
import com.github.sparkzxl.core.resource.SwaggerStaticResource;
import com.github.sparkzxl.open.component.RestAuthenticationEntryPoint;
import com.github.sparkzxl.open.component.RestfulAccessDeniedHandler;
import com.github.sparkzxl.open.filter.PermitAuthenticationFilter;
import com.github.sparkzxl.open.properties.SecurityProperties;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import javax.servlet.Filter;
import java.util.List;

/**
 * description: 安全认证
 *
 * @author zhouxinlei
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class WebSecurityAutoConfig extends WebSecurityConfigurerAdapter {

    @Autowired(required = false)
    private UserDetailsService userDetailsService;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired(required = false)
    private LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PermitAuthenticationFilter permitAuthenticationFilter() {
        return new PermitAuthenticationFilter();
    }

    @Bean
    public PermitAllSecurityConfig permitAllSecurityConfig() {
        PermitAllSecurityConfig permitAllSecurityConfig = new PermitAllSecurityConfig();
        permitAllSecurityConfig.setPermitAuthenticationFilter(permitAuthenticationFilter());
        return permitAllSecurityConfig;

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
        http.authorizeRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll();
        if (!securityProperties.isCsrf()) {
            http.csrf().disable();
        }
        if (securityProperties.isCustomLogout()) {
            http.logout().logoutUrl("/customLogout")
                    .logoutSuccessHandler(logoutSuccessHandler)
                    .clearAuthentication(true)
                    .invalidateHttpSession(true);
        }
        if (securityProperties.isRestAuthentication()) {
            if (securityProperties.isCustomLogin()) {
                http.authorizeRequests()
                        .anyRequest().authenticated()
                        .and()
                        .formLogin()
                        .loginPage("/authentication/require")
                        .loginProcessingUrl("/authentication/form")
                        .permitAll().and()
                        .httpBasic()
                        .and()
                        .exceptionHandling()
                        .accessDeniedHandler(restfulAccessDeniedHandler)
                        .authenticationEntryPoint(restAuthenticationEntryPoint);
            } else {
                http.authorizeRequests()
                        .anyRequest().authenticated()
                        .and()
                        .formLogin()
                        .permitAll().and()
                        .httpBasic()
                        .and()
                        .exceptionHandling()
                        .accessDeniedHandler(restfulAccessDeniedHandler)
                        .authenticationEntryPoint(restAuthenticationEntryPoint);
            }
        } else {
            if (securityProperties.isCustomLogin()) {
                http.authorizeRequests()
                        .anyRequest().authenticated()
                        .and()
                        .formLogin()
                        .loginPage("/authentication/require")
                        .loginProcessingUrl("/authentication/form")
                        .permitAll().and()
                        .httpBasic()
                        .and()
                        .exceptionHandling()
                        .accessDeniedHandler(restfulAccessDeniedHandler);
            } else {
                http.authorizeRequests()
                        .anyRequest().authenticated()
                        .and()
                        .formLogin()
                        .permitAll().and()
                        .httpBasic()
                        .and()
                        .exceptionHandling()
                        .accessDeniedHandler(restfulAccessDeniedHandler);
            }
            if (StringUtils.isNotEmpty(securityProperties.getPreHandleFilter())) {
                Filter bean = (Filter) applicationContext.getBean(securityProperties.getPreHandleFilter());
                http.addFilterBefore(bean, UsernamePasswordAuthenticationFilter.class);
            }
        }
    }

    @Override
    public void configure(WebSecurity web) {
        List<String> ignoreStaticPatterns = Lists.newArrayList();
        ignoreStaticPatterns.addAll(SwaggerStaticResource.EXCLUDE_STATIC_PATTERNS);
        if (CollectionUtils.isNotEmpty(securityProperties.getIgnoreStaticPatterns())) {
            ignoreStaticPatterns.addAll(securityProperties.getIgnoreStaticPatterns());
        }
        web.ignoring().antMatchers(ArrayUtil.toArray(ignoreStaticPatterns, String.class));
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        web.httpFirewall(firewall);
    }

}
