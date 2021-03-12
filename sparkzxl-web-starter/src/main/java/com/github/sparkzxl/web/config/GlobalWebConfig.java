package com.github.sparkzxl.web.config;

import com.github.sparkzxl.web.interceptor.ResponseResultInterceptor;
import com.github.sparkzxl.web.properties.WebProperties;
import com.github.sparkzxl.web.support.GlobalExceptionHandler;
import com.github.sparkzxl.web.support.ResponseResultHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * description: WebConfig全局配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:43:12
 */
@Configuration
@Import({ResponseResultHandler.class, GlobalExceptionHandler.class})
@EnableConfigurationProperties(WebProperties.class)
public class GlobalWebConfig implements WebMvcConfigurer {

    @Autowired
    private WebProperties webProperties;
    @Autowired
    protected ApplicationContext applicationContext;

    @Bean
    public ResponseResultInterceptor responseResultInterceptor() {
        return new ResponseResultInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String interceptor = webProperties.getInterceptor();
        if (StringUtils.isNotEmpty(interceptor)) {
            registry.addInterceptor((HandlerInterceptor) applicationContext.getBean(interceptor)).addPathPatterns("/**").order(-99);
        }
    }
}
