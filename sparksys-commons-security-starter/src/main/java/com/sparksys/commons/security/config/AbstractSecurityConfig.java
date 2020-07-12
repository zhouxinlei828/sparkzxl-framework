package com.sparksys.commons.security.config;

import com.sparksys.commons.core.utils.collection.ListUtils;
import com.sparksys.commons.security.authorization.DynamicAccessDecisionManager;
import com.sparksys.commons.security.component.DynamicSecurityMetadataSource;
import com.sparksys.commons.security.component.JwtAuthenticationTokenFilter;
import com.sparksys.commons.security.component.RestAuthenticationEntryPoint;
import com.sparksys.commons.security.component.RestfulAccessDeniedHandler;
import com.sparksys.commons.security.filter.DynamicSecurityFilter;
import com.sparksys.commons.security.props.IgnoreUrlsProperties;
import com.sparksys.commons.security.registry.SecurityRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * description: Spring Security 抽象配置，具体可在实际业务中注入
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:35:26
 */
public abstract class AbstractSecurityConfig extends WebSecurityConfigurerAdapter {

    private final RestfulAccessDeniedHandler restfulAccessDeniedHandler = new RestfulAccessDeniedHandler();

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint = new RestAuthenticationEntryPoint();

    @Value("${sparksys.security.enableJwtFilter}")
    private boolean enableJwtFilter;

    @Value("${sparksys.security.dynamicSecurity}")
    private boolean dynamicSecurity;

    /**
     * Security api放行配置
     *
     * @return SecurityRegistry
     */
    protected abstract SecurityRegistry securityRegistry();

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        String[] excludeStaticPatterns = ListUtils.stringToArray(SecurityRegistry.excludeStaticPatterns);
        web.ignoring().antMatchers(excludeStaticPatterns);
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        web.httpFirewall(firewall);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity
                .authorizeRequests();
        SecurityRegistry securityRegistry = securityRegistry();
        for (String url : securityRegistry.getExcludePatterns()) {
            registry.antMatchers(url).permitAll();
        }
        // 任何请求需要身份认证
        registry.and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                // 关闭跨站请求防护及不使用session
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 自定义权限拒绝处理类
                .and()
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint);

        if (dynamicSecurity) {
            registry.and().addFilterBefore(dynamicSecurityFilter(), FilterSecurityInterceptor.class);
        }
        if (enableJwtFilter) {
            registry.and().addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnProperty(name = {"sparksys.security.enableJwtFilter"}, havingValue = "true")
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Security忽略请求
     *
     * @return IgnoreUrlsProperties
     */
    public abstract IgnoreUrlsProperties ignoreUrlsProperties();


    @ConditionalOnBean(name = "dynamicSecurityService")
    @ConditionalOnProperty(name = {"sparksys.security.dynamicSecurity"}, havingValue = "true")
    @Bean
    public DynamicAccessDecisionManager dynamicAccessDecisionManager() {
        return new DynamicAccessDecisionManager();
    }

    @ConditionalOnBean(name = "dynamicSecurityService")
    @ConditionalOnProperty(name = {"sparksys.security.dynamicSecurity"}, havingValue = "true")
    @Bean
    public DynamicSecurityFilter dynamicSecurityFilter() {
        return new DynamicSecurityFilter(dynamicSecurityMetadataSource());
    }

    @ConditionalOnBean(name = "dynamicSecurityService")
    @ConditionalOnProperty(name = {"sparksys.security.dynamicSecurity"}, havingValue = "true")
    @Bean
    public DynamicSecurityMetadataSource dynamicSecurityMetadataSource() {
        return new DynamicSecurityMetadataSource();
    }

}
