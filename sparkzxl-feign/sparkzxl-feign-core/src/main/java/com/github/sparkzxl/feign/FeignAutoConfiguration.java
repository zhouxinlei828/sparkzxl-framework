package com.github.sparkzxl.feign;

import com.github.sparkzxl.feign.interceptor.FeignHeaderRequestInterceptor;
import com.github.sparkzxl.feign.logger.InfoFeignLoggerFactory;
import com.github.sparkzxl.feign.properties.FeignProperties;
import com.github.sparkzxl.feign.support.FeignExceptionHandler;
import feign.Feign;
import feign.Logger;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * description: OpenFeign 配置
 *
 * @author zhouxinlei
 */
@Configuration
@Import(FeignExceptionHandler.class)
@EnableConfigurationProperties(FeignProperties.class)
@ConditionalOnClass(Feign.class)
@AutoConfigureAfter(EnableFeignClients.class)
public class FeignAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(FeignLoggerFactory.class)
    public FeignLoggerFactory feignLoggerFactory() {
        return new InfoFeignLoggerFactory();
    }

    @Bean
    @Profile({"dev", "test"})
    Logger.Level devFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    @Profile({"uat", "pre", "prod"})
    Logger.Level prodFeignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public DateFormatRegister dateFormatRegister() {
        return new DateFormatRegister();
    }

    /**
     * feign 支持MultipartFile上传文件
     *
     * @return Encoder
     */
    @Bean
    public Encoder feignFormEncoder() {
        List<HttpMessageConverter<?>> converters = new RestTemplate().getMessageConverters();
        return new SpringFormEncoder(new SpringEncoder(() -> new HttpMessageConverters(converters)));
    }

    /**
     * feign client 请求头传播
     */
    @ConditionalOnMissingBean
    @Bean
    public FeignHeaderRequestInterceptor feignHeaderRequestInterceptor() {
        return new FeignHeaderRequestInterceptor();
    }

}
