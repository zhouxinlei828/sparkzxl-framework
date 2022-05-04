package com.github.sparkzxl.feign.resilience4j.autoconfigure;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4jBulkheadProvider;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

/**
 * description: Resilience4j Config
 *
 * @author zhouxinlei
 * @since 2022-04-03 11:47:47
 */
public class Resilience4jAutoConfig {

    @Bean
    public Resilience4JCircuitBreakerFactory resilience4JCircuitBreakerFactory(CircuitBreakerRegistry circuitBreakerRegistry, TimeLimiterRegistry timeLimiterRegistry, Resilience4jBulkheadProvider bulkheadProvider) {
        return new Resilience4JCircuitBreakerFactory(circuitBreakerRegistry, timeLimiterRegistry, bulkheadProvider);
    }
}