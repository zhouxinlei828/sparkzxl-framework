package com.github.sparkzxl.web.config;

import com.github.sparkzxl.core.utils.ListUtils;
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
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description: WebConfig全局配置
 *
 * @author zhouxinlei
 */
@Configuration
@Import({ResponseResultHandler.class, GlobalExceptionHandler.class})
@EnableConfigurationProperties(WebProperties.class)
public class GlobalWebConfig implements WebMvcConfigurer {

    private WebProperties webProperties;

    private ApplicationContext applicationContext;

    @Autowired
    public void setWebProperties(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public ResponseResultInterceptor responseResultInterceptor() {
        return new ResponseResultInterceptor();
    }

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String interceptorStr = webProperties.getInterceptor();
        if (StringUtils.isNotEmpty(interceptorStr)) {
            List<String> interceptorList = ListUtils.stringToList(interceptorStr);
            AtomicInteger atomicInteger = new AtomicInteger(-99);
            interceptorList.forEach(interceptor -> {
                int increment = atomicInteger.getAndIncrement();
                registry.addInterceptor((HandlerInterceptor) applicationContext.getBean(interceptor))
                        .addPathPatterns("/**").order(increment);
            });
        } else {
            registry.addInterceptor(responseResultInterceptor())
                    .addPathPatterns("/**").order(-99);
        }
    }

}
