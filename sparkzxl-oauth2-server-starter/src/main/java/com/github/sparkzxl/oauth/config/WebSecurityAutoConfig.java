package com.github.sparkzxl.oauth.config;

import cn.hutool.core.util.ArrayUtil;
import com.github.sparkzxl.core.resource.SwaggerStaticResource;
import com.github.sparkzxl.core.utils.ListUtils;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import com.github.sparkzxl.oauth.component.RestAuthenticationEntryPoint;
import com.github.sparkzxl.oauth.component.RestfulAccessDeniedHandler;
import com.github.sparkzxl.oauth.filter.JwtAuthenticationTokenFilter;
import com.github.sparkzxl.oauth.properties.SecurityProperties;
import lombok.AllArgsConstructor;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * description: SpringSecurity配置
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 12:29:21
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties({SecurityProperties.class})
@EnableWebSecurity
public class WebSecurityAutoConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProperties securityProperties;

    private final JwtTokenService jwtTokenService;

    private final UserDetailsService userDetailsService;

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter(jwtTokenService, userDetailsService);
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
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and().addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    }

}
