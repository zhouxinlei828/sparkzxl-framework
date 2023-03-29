package com.github.sparkzxl.feign.resilience4j.autoconfigure;

import com.github.sparkzxl.feign.resilience4j.FeignDecoratorBuilderInterceptor;
import com.github.sparkzxl.feign.resilience4j.decoder.DefaultErrorDecoder;
import feign.Feign;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.control.Try;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

/**
 * description: 默认feign client 配置
 *
 * @author zhouxinlei
 * @since 2022-04-04 12:21:42
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class DefaultFeignClientConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new DefaultErrorDecoder();
    }

    @Bean
    public FeignDecorators.Builder defaultBuilder(Environment environment, RetryRegistry retryRegistry) {
        String name = environment.getProperty("feign.client.name");
        assert name != null;
        Retry retry = Try.of(() -> retryRegistry.retry(name, name)).getOrElseGet(throwable -> retryRegistry.retry(name));
        //覆盖其中的异常判断，只针对 feign.RetryableException 进行重试，所有需要重试的异常我们都在 DefaultErrorDecoder 以及 Resilience4jFeignClient 中封装成了 RetryableException
        retry = Retry.of(name,
                RetryConfig.from(retry.getRetryConfig()).retryOnException(throwable -> throwable instanceof feign.RetryableException)
                        .build());
        return FeignDecorators.builder().withRetry(retry);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Feign.Builder resilience4jFeignBuilder(
            List<FeignDecoratorBuilderInterceptor> feignDecoratorBuilderInterceptors,
            FeignDecorators.Builder builder,
            @Autowired List<RequestInterceptor> requestInterceptorList
    ) {
        feignDecoratorBuilderInterceptors.forEach(feignDecoratorBuilderInterceptor -> feignDecoratorBuilderInterceptor.intercept(builder));
        return Resilience4jFeign.builder(builder.build())
                .requestInterceptors(requestInterceptorList)
                .decode404();
    }

}
