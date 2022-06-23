package com.github.sparkzxl.feign.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.cloud.client.loadbalancer.Request;

/**
 * description: CircuitBreaker Extractor
 *
 * @author zhouxinlei
 * @since 2022-05-20 10:40:05
 */
public interface CircuitBreakerExtractor<T> {

    /**
     * 通过负载均衡请求，以及实例信息，获取对应的 CircuitBreaker
     *
     * @param circuitBreakerRegistry CircuitBreaker 实例的工厂
     * @param request                请求
     * @param host                   host
     * @param port                   端口
     * @return CircuitBreaker
     */
    CircuitBreaker getCircuitBreaker(
            CircuitBreakerRegistry circuitBreakerRegistry,
            Request<T> request,
            String host,
            int port
    );

}
