package com.github.sparkzxl.feign;

import com.github.sparkzxl.feign.hystrix.ThreadLocalHystrixConcurrencyStrategy;
import com.github.sparkzxl.feign.interceptor.FeignAddHeaderRequestInterceptor;
import com.github.sparkzxl.feign.properties.FeignProperties;
import com.github.sparkzxl.feign.support.FeignExceptionHandler;
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
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
@Import(FeignExceptionHandler.class)
@EnableConfigurationProperties(FeignProperties.class)
public class FeignAutoConfiguration {

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
     */
    @Bean
    public ThreadLocalHystrixConcurrencyStrategy getThreadLocalHystrixConcurrencyStrategy() {
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
        public Feign.Builder feignHystrixBuilder(List<RequestInterceptor> requestInterceptor) {
            return HystrixFeign.builder()
                    .decode404()
                    .requestInterceptors(requestInterceptor);
        }

        /**
         * feign client 请求头传播
         */
        @ConditionalOnMissingBean
        @Bean
        public FeignAddHeaderRequestInterceptor getClientTokenInterceptor() {
            return new FeignAddHeaderRequestInterceptor();
        }
    }

}
