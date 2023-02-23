package com.github.sparkzxl.web.config;

import com.github.sparkzxl.core.constant.Constant;
import com.github.sparkzxl.web.interceptor.WebRequestInterceptor;
import com.github.sparkzxl.web.properties.WebProperties;
import com.github.sparkzxl.web.support.DefaultExceptionHandler;
import com.github.sparkzxl.web.support.ResponseResultAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * description: WebConfig全局配置
 *
 * @author zhouxinlei
 */
@Configuration
@Import({ResponseResultAdvice.class, DefaultExceptionHandler.class})
@EnableConfigurationProperties(WebProperties.class)
@Slf4j
public class DefaultWebConfig implements WebMvcConfigurer {

    @Autowired
    private WebProperties webProperties;


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 解决 String 统一封装RestBody的问题
        HttpMessageConverter<?> httpMessageConverter = converters.get(7);
        if (!(httpMessageConverter instanceof MappingJackson2HttpMessageConverter)) {
            // 确保正确，如果有改动就重新debug
            throw new RuntimeException("MappingJackson2HttpMessageConverter is not here");
        }
        converters.add(0, httpMessageConverter);
    }

    @Bean
    public WebRequestInterceptor webRequestInterceptor() {
        return new WebRequestInterceptor(webProperties);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webRequestInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(Constant.EXCLUDE_STATIC_PATTERNS);
    }

}
