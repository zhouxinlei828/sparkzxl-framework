package com.github.sparkzxl.security.config;

import com.github.sparkzxl.core.resource.SwaggerStaticResource;
import com.github.sparkzxl.core.utils.ListUtils;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import com.github.sparkzxl.security.authorization.DynamicAccessDecisionManager;
import com.github.sparkzxl.security.endpoint.AuthorizationEndPoint;
import com.github.sparkzxl.security.filter.JwtAuthenticationTokenFilter;
import com.github.sparkzxl.security.intercept.DynamicSecurityMetadataSource;
import com.github.sparkzxl.security.properties.SecurityProperties;
import com.github.sparkzxl.security.component.RestAuthenticationEntryPoint;
import com.github.sparkzxl.security.component.RestfulAccessDeniedHandler;
import com.github.sparkzxl.security.filter.DynamicSecurityFilter;
import com.github.sparkzxl.security.service.AbstractSecurityLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.List;

/**
 * description: Spring Security 配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:35:26
 */

@Configuration
@EnableConfigurationProperties({SecurityProperties.class})
@EnableWebSecurity
@Slf4j
public class WebSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    private final SecurityProperties securityProperties;

    @Autowired(required = false)
    private JwtTokenService jwtTokenService;

    @Autowired(required = false)
    private UserDetailsService userDetailsService;
    @Autowired(required = false)
    private AbstractSecurityLoginService abstractSecurityLoginService;

    public WebSecurityAutoConfiguration(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void configure(WebSecurity web) {
        String[] excludeStaticPatterns = ListUtils.listToArray(SwaggerStaticResource.EXCLUDE_STATIC_PATTERNS);
        web.ignoring().antMatchers(excludeStaticPatterns);
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        web.httpFirewall(firewall);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity
                .authorizeRequests();
        List<String> excludePatterns = securityProperties.getIgnorePatterns();
        for (String url : excludePatterns) {
            registry.antMatchers(url).permitAll();
        }
        RestfulAccessDeniedHandler restfulAccessDeniedHandler = new RestfulAccessDeniedHandler();
        RestAuthenticationEntryPoint restAuthenticationEntryPoint = new RestAuthenticationEntryPoint();
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
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        if (securityProperties.isBuiltInPermissions()){
            registry.and().addFilterBefore(dynamicSecurityFilter(), FilterSecurityInterceptor.class);
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter(jwtTokenService, userDetailsService);
    }

    @Bean
    @ConditionalOnProperty(name = {"sparkzxl.security.built-in-permissions"}, havingValue = "true", matchIfMissing = true)
    public DynamicAccessDecisionManager dynamicAccessDecisionManager() {
        log.info("DynamicAccessDecisionManager registered success! ");
        return new DynamicAccessDecisionManager();
    }

    @Bean
    @ConditionalOnProperty(name = {"sparkzxl.security.built-in-permissions"}, havingValue = "true", matchIfMissing = true)
    public DynamicSecurityFilter dynamicSecurityFilter() {
        return new DynamicSecurityFilter(dynamicSecurityMetadataSource(), securityProperties);
    }

    @Bean
    @ConditionalOnProperty(name = {"sparkzxl.security.built-in-permissions"}, havingValue = "true", matchIfMissing = true)
    public DynamicSecurityMetadataSource dynamicSecurityMetadataSource() {
        return new DynamicSecurityMetadataSource();
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "sparkzxl.security.enableLogin", havingValue = "true")
    public AuthorizationEndPoint authorizationEndPoint() {
        return new AuthorizationEndPoint(abstractSecurityLoginService);
    }
}
