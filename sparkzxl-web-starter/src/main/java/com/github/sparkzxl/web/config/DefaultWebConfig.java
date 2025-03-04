package com.github.sparkzxl.web.config;

import com.github.sparkzxl.annotation.ApiLimit;
import com.github.sparkzxl.core.constant.Constant;
import com.github.sparkzxl.web.aop.ApiLimitAnnotationAdvisor;
import com.github.sparkzxl.web.aop.ApiLimitInterceptor;
import com.github.sparkzxl.web.interceptor.WebRequestInterceptor;
import com.github.sparkzxl.web.properties.WebProperties;
import com.github.sparkzxl.web.support.DefaultExceptionHandler;
import com.github.sparkzxl.web.support.ResponseResultAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
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


    /**
     * 交换MappingJackson2HttpMessageConverter与第一位元素
     * 让返回值类型为String的接口能正常返回包装结果
     *
     * @param converters initially an empty list of converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (int i = 0; i < converters.size(); i++) {
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) converters.get(i);
                converters.set(i, converters.get(0));
                converters.set(0, mappingJackson2HttpMessageConverter);
                break;
            }
        }
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

    @Bean
    @ConditionalOnMissingBean
    public ApiLimitInterceptor apiLimitInterceptor() {
        return new ApiLimitInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiLimitAnnotationAdvisor apiLimitAnnotationAdvisor(ApiLimitInterceptor apiLimitInterceptor) {
        return new ApiLimitAnnotationAdvisor(apiLimitInterceptor, ApiLimit.class, Ordered.HIGHEST_PRECEDENCE);
    }

}
