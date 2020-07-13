package com.sparksys.commons.cloud.config;

import com.sparksys.commons.cloud.utils.DateFormatRegister;
import com.sparksys.commons.cloud.hystrix.ThreadLocalHystrixConcurrencyStrategy;
import com.sparksys.commons.cloud.interceptor.FeignAddHeaderRequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * description: OpenFeign 配置
 *
 * @author: zhouxinlei
 * @date: 2020-07-12 16:32:22
 */
@Configuration
@ConditionalOnClass(FeignAutoConfiguration.class)
public class OpenFeignAutoConfiguration {

    public OpenFeignAutoConfiguration() {
    }

    /**
     * 在feign调用方配置， 解决入参和出参是 date 类型
     *
     * @return DateFormatRegister
     */
    @Bean
    public DateFormatRegister dateFormatRegister() {
        return new DateFormatRegister();
    }

    /**
     * feign 表单编码
     *
     * @return Encoder
     */
    @Bean
    public Encoder feignFormEncoder() {
        List<HttpMessageConverter<?>> converters = new RestTemplate().getMessageConverters();
        ObjectFactory<HttpMessageConverters> factory = () -> new HttpMessageConverters(converters);
        return new SpringFormEncoder(new SpringEncoder(factory));
    }

    /**
     * feign client 请求头传播
     *
     * @return FeignAddHeaderRequestInterceptor
     */
    @Bean
    public FeignAddHeaderRequestInterceptor getClientTokenInterceptor() {
        return new FeignAddHeaderRequestInterceptor();
    }

    /**
     * 本地线程 Hystrix并发策略
     *
     * @return ThreadLocalHystrixConcurrencyStrategy
     */
    @Bean
    public ThreadLocalHystrixConcurrencyStrategy getThreadLocalHystrixConcurrencyStrategy() {
        return new ThreadLocalHystrixConcurrencyStrategy();
    }
}