package com.github.sparkzxl.web.config;

import com.github.sparkzxl.web.aspect.ResponseResultStatusAspect;
import com.github.sparkzxl.web.interceptor.HeaderThreadLocalInterceptor;
import com.github.sparkzxl.web.properties.WebProperties;
import com.github.sparkzxl.web.support.DefaultExceptionHandler;
import com.github.sparkzxl.web.support.ResponseResultAdvice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
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
@Import({ResponseResultAdvice.class, DefaultExceptionHandler.class})
@EnableConfigurationProperties(WebProperties.class)
@Slf4j
public class DefaultWebConfig implements WebMvcConfigurer {

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
    public HeaderThreadLocalInterceptor responseResultInterceptor() {
        return new HeaderThreadLocalInterceptor();
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
        List<Class<? extends HandlerInterceptor>> interceptorList = webProperties.getInterceptor();
        AtomicInteger atomicInteger = new AtomicInteger(-99);
        interceptorList.forEach(interceptor -> {
            int increment = atomicInteger.getAndIncrement();
            registry.addInterceptor((HandlerInterceptor) applicationContext.getBean(interceptor))
                    .addPathPatterns("/**").order(increment);
            log.info("已加载拦截器：[{}]", ClassUtils.getName(interceptor));
        });
    }

    @Bean
    public ResponseResultStatusAspect responseResultStatusAspect() {
        ResponseResultStatusAspect responseResultStatusAspect = new ResponseResultStatusAspect();
        responseResultStatusAspect.setEnableTransferStatus(webProperties.isEnableTransferStatus());
        responseResultStatusAspect.setTransferExceptionStatus(webProperties.isTransferException());
        return responseResultStatusAspect;
    }
}
