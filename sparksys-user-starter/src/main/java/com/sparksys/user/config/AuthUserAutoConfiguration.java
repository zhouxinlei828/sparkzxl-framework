package com.sparksys.user.config;

import com.sparksys.user.resolver.AuthUserArgumentResolver;
import com.sparksys.user.service.IAuthUserInfoService;
import com.sparksys.user.service.impl.AuthUserInfoServiceServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * description: 全局用户配置类
 *
 * @author: zhouxinlei
 * @date: 2020-07-13 14:42:17
 */
@Configuration
@Slf4j
public class AuthUserAutoConfiguration implements WebMvcConfigurer {

    @Bean
    public IAuthUserInfoService authUserInfoService() {
        return new AuthUserInfoServiceServiceImpl();
    }

    @Bean
    public AuthUserArgumentResolver globalUserArgumentResolver() {
        log.info("Automatic injection of global user information acquisition");
        return new AuthUserArgumentResolver(authUserInfoService());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(globalUserArgumentResolver());
    }

}
