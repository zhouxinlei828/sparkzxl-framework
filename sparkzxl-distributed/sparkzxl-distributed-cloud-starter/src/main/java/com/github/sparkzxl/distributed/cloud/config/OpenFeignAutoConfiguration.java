package com.github.sparkzxl.distributed.cloud.config;

import com.github.sparkzxl.distributed.cloud.hystrix.ThreadLocalHystrixConcurrencyStrategy;
import com.github.sparkzxl.distributed.cloud.interceptor.FeignAddHeaderRequestInterceptor;
import com.netflix.hystrix.HystrixCommand;
import feign.Feign;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.hystrix.HystrixFeign;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * description: OpenFeign 配置
 *
 * @author zhouxinlei
 */
@Configuration
public class OpenFeignAutoConfiguration {

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
     * 本地线程 Hystrix并发策略
     *
     * @return ThreadLocalHystrixConcurrencyStrategy
     */
    @Bean
    public ThreadLocalHystrixConcurrencyStrategy threadLocalHystrixConcurrencyStrategy() {
        return new ThreadLocalHystrixConcurrencyStrategy();
    }

    @Configuration("hystrixFeignConfiguration")
    @ConditionalOnClass({HystrixCommand.class, HystrixFeign.class})
    protected static class HystrixFeignConfiguration {

        /**
         * 覆盖了 org.springframework.cloud.openfeign.FeignClientsConfiguration 的配置
         */
        @Bean
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        @ConditionalOnProperty("feign.hystrix.enabled")
        public Feign.Builder feignHystrixBuilder(RequestInterceptor requestInterceptor) {
            return HystrixFeign.builder()
                    .decode404()
                    .requestInterceptor(requestInterceptor);
        }

        /**
         * feign client 请求头传播
         *
         * @return FeignAddHeaderRequestInterceptor
         */
        @ConditionalOnMissingBean
        @Bean
        public FeignAddHeaderRequestInterceptor getClientTokenInterceptor() {
            return new FeignAddHeaderRequestInterceptor();
        }
    }
}
