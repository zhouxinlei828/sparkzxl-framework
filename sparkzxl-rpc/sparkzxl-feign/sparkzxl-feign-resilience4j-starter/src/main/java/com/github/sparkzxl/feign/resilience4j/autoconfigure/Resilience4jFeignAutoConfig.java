package com.github.sparkzxl.feign.resilience4j.autoconfigure;

import com.github.sparkzxl.feign.resilience4j.CircuitBreakerExtractor;
import com.github.sparkzxl.feign.resilience4j.FeignRequestCircuitBreakerExtractor;
import com.github.sparkzxl.feign.resilience4j.client.FeignBlockingLoadBalancerClientDelegate;
import com.github.sparkzxl.feign.resilience4j.client.Resilience4jFeignClient;
import feign.okhttp.OkHttpClient;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * description: resilience4j feign auto config¬
 *
 * @author zhouxinlei
 * @since 2022-04-04 11:57:28
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "feign.resilience4j.enabled", matchIfMissing = true)
public class Resilience4jFeignAutoConfig {

    @Bean
    public OkHttpClient okHttpClient(@Autowired(required = false) okhttp3.OkHttpClient httpClient) {
        return new OkHttpClient(httpClient);
    }

    @Bean
    @Primary
    public FeignBlockingLoadBalancerClientDelegate feignBlockingLoadBalancerCircuitBreakableClient(
            OkHttpClient okHttpClient,
            //为何使用 ObjectProvider 请参考 FeignBlockingLoadBalancerClientDelegate 源码的注释
            ObjectProvider<LoadBalancerClient> loadBalancerClientProvider,
            //resilience4j 的线程隔离
            @Autowired(required = false) ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry,
            //resilience4j 的断路器
            @Autowired(required = false) CircuitBreakerRegistry circuitBreakerRegistry,
            //负载均衡属性
            @Autowired(required = false) LoadBalancerProperties loadBalancerProperties,
            //为何使用这个不直接用 FeignBlockingLoadBalancerClient 请参考 FeignBlockingLoadBalancerClientDelegate 的注释
            @Autowired(required = false) LoadBalancerClientFactory loadBalancerClientFactory) {
        Resilience4jFeignClient resilience4jFeignClient = new Resilience4jFeignClient(
                okHttpClient,
                threadPoolBulkheadRegistry,
                circuitBreakerRegistry);
        return new FeignBlockingLoadBalancerClientDelegate(
                resilience4jFeignClient,
                loadBalancerClientProvider,
                loadBalancerProperties,
                loadBalancerClientFactory
        );
    }

}
