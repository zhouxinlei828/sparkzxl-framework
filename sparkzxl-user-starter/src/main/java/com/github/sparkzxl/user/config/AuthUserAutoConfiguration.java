package com.github.sparkzxl.user.config;

import com.github.sparkzxl.user.manager.DefaultUserStateManager;
import com.github.sparkzxl.user.manager.UserStateManager;
import com.github.sparkzxl.user.resolver.AuthUserArgumentResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * description: 用户配置类
 *
 * @author zhouxinlei
 */
@Configuration
@Slf4j
public class AuthUserAutoConfiguration implements WebMvcConfigurer {

    @Bean
    public UserStateManager userStateManager() {
        return new DefaultUserStateManager();
    }

    @Bean
    public AuthUserArgumentResolver authUserArgumentResolver() {
        log.info("Automatic injection of global user information acquisition");
        return new AuthUserArgumentResolver(userStateManager());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authUserArgumentResolver());
    }

}
