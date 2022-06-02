package com.github.sparkzxl.feign;

import com.github.sparkzxl.feign.decoder.DefaultErrorDecoder;
import com.github.sparkzxl.feign.decoder.FeignRequestDecoder;
import com.github.sparkzxl.feign.exception.ExceptionDefinitionLocator;
import com.github.sparkzxl.feign.exception.ExceptionDefinitionLocatorImpl;
import com.github.sparkzxl.feign.exception.ExceptionPredicateFactory;
import com.github.sparkzxl.feign.exception.NormalRespExceptionPredicateFactory;
import com.github.sparkzxl.feign.interceptor.FeignHeaderRequestInterceptor;
import com.github.sparkzxl.feign.logger.InfoFeignLoggerFactory;
import com.github.sparkzxl.feign.properties.FeignProperties;
import com.github.sparkzxl.feign.support.FeignExceptionHandler;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
public class FeignAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(FeignLoggerFactory.class)
    public FeignLoggerFactory getInfoFeignLoggerFactory() {
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

    @Bean
    public NormalRespExceptionPredicateFactory normalRespExceptionPredicateFactory() {
        return new NormalRespExceptionPredicateFactory();
    }

    @Bean
    public ExceptionDefinitionLocator exceptionDefinitionLocator(FeignProperties feignProperties,
                                                                 List<ExceptionPredicateFactory> predicateFactories) {
        return new ExceptionDefinitionLocatorImpl(feignProperties.getError(), predicateFactories);
    }

    /**
     * Feign解码器
     *
     * @param exceptionDefinitionLocator 异常定义定位器
     * @return Decoder
     */
    @Bean
    public Decoder feignDecoder(ExceptionDefinitionLocator exceptionDefinitionLocator) {
        List<HttpMessageConverter<?>> converters = new RestTemplate().getMessageConverters();
        ObjectFactory<HttpMessageConverters> factory = () -> new HttpMessageConverters(converters);
        FeignRequestDecoder feignRequestDecoder = new FeignRequestDecoder(factory);
        feignRequestDecoder.setExceptionDefinitionLocator(exceptionDefinitionLocator);
        return feignRequestDecoder;
    }


    @Bean
    public ErrorDecoder errorDecoder() {
        return new DefaultErrorDecoder();
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
