package com.github.sparkzxl.feign.hystrix;

import com.github.sparkzxl.feign.hystrix.strategy.ThreadLocalHystrixConcurrencyStrategy;
import com.netflix.hystrix.HystrixCommand;
import feign.Feign;
import feign.RequestInterceptor;
import feign.hystrix.HystrixFeign;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * description: HystrixFeignConfiguration
 *
 * @author zhouxinlei
 * @since 2022-04-06 11:09:23
 */
@Configuration("hystrixFeignConfiguration")
@ConditionalOnClass({HystrixCommand.class, HystrixFeign.class})
public class HystrixFeignConfiguration {

    /**
     * 本地线程 Hystrix并发策略
     */
    @Bean
    @ConditionalOnProperty("feign.hystrix.enabled")
    public ThreadLocalHystrixConcurrencyStrategy threadLocalHystrixConcurrencyStrategy() {
        return new ThreadLocalHystrixConcurrencyStrategy();
    }

    /**
     * 覆盖了 org.springframework.cloud.openfeign.FeignClientsConfiguration 的配置
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnProperty("feign.hystrix.enabled")
    public Feign.Builder feignHystrixBuilder(List<RequestInterceptor> requestInterceptorList) {
        return HystrixFeign.builder()
                .requestInterceptors(requestInterceptorList)
                .decode404();
    }

}
