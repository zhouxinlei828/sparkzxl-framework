package com.github.sparkzxl.feign.config;

import com.github.sparkzxl.feign.interceptor.FeignAddHeaderRequestInterceptor;
import com.github.sparkzxl.feign.properties.FeignProperties;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * description: OpenFeign 配置
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties(FeignProperties.class)
public class OpenFeignAutoConfiguration {

    @Autowired
    private FeignProperties feignProperties;

    /**
     * feign 支持MultipartFile上传文件
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
    @ConditionalOnMissingBean
    @Bean
    public FeignAddHeaderRequestInterceptor feignAddHeaderRequestInterceptor() {
        return new FeignAddHeaderRequestInterceptor(feignProperties.isEnable());
    }
}
