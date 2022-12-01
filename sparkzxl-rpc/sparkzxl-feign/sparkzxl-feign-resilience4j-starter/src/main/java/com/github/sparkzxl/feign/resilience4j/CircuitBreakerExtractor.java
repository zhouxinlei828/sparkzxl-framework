package com.github.sparkzxl.feign.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.cloud.client.loadbalancer.Request;

/**
 * description: 断路器获取
 *
 * @author zhouxinlei
 * @since 2022-12-01 11:01:53
 */
public interface CircuitBreakerExtractor<T> {

    /**
     * 通过负载均衡请求，以及实例信息，获取对应的 CircuitBreaker
     *
     * @param circuitBreakerRegistry 断路器实例工厂
     * @param request                request
     * @param host                   host
     * @param port                   port
     * @return CircuitBreaker
     */
    CircuitBreaker getCircuitBreaker(
            CircuitBreakerRegistry circuitBreakerRegistry,
            Request<T> request,
            String host,
            int port
    );

    /**
     * 获取traceId
     *
     * @param request request
     * @return String
     */
    String getTraceId(Request<T> request);
}
