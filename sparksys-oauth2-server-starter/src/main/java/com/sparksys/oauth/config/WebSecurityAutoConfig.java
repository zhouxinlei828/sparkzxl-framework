package com.sparksys.oauth.config;

import cn.hutool.core.util.ArrayUtil;
import com.sparksys.core.resource.SwaggerStaticResource;
import com.sparksys.core.utils.ListUtils;
import com.sparksys.oauth.component.RestAuthenticationEntryPoint;
import com.sparksys.oauth.component.RestfulAccessDeniedHandler;
import com.sparksys.oauth.properties.SecurityProperties;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * description: SpringSecurity配置
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 12:29:21
 */
@Configuration
@EnableConfigurationProperties({SecurityProperties.class})
@EnableWebSecurity
public class WebSecurityAutoConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProperties securityProperties;

    public WebSecurityAutoConfig(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        String[] excludeStaticPatterns = ListUtils.stringToArray(SwaggerStaticResource.EXCLUDE_STATIC_PATTERNS);
        web.ignoring().antMatchers(excludeStaticPatterns);
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        web.httpFirewall(firewall);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        RestfulAccessDeniedHandler restfulAccessDeniedHandler = new RestfulAccessDeniedHandler();
        RestAuthenticationEntryPoint restAuthenticationEntryPoint = new RestAuthenticationEntryPoint();
        httpSecurity
                .authorizeRequests().requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .antMatchers(ArrayUtil.toArray(securityProperties.getIgnorePatterns(), String.class)).permitAll()
                .anyRequest().authenticated()
                // 关闭跨站请求防护及不使用session
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint);

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
