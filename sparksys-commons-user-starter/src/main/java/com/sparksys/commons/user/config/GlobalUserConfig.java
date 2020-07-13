package com.sparksys.commons.user.config;

import com.sparksys.commons.user.resolver.GlobalUserArgumentResolver;
import com.sparksys.commons.user.service.IGlobalUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * description: 全局用户配置类
 *
 * @author: zhouxinlei
 * @date: 2020-07-13 14:42:17
 */
@Configuration
@Slf4j
public class GlobalUserConfig implements WebMvcConfigurer {

    @Resource
    private IGlobalUserService globalUserService;

    @Bean
    public GlobalUserArgumentResolver globalUserArgumentResolver() {
        log.info("全局用户信息获取");
        return new GlobalUserArgumentResolver(globalUserService);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(globalUserArgumentResolver());
    }

}
