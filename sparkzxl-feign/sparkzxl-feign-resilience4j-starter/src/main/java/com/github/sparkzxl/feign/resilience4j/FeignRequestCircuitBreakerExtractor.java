package com.github.sparkzxl.feign.resilience4j;

import com.alibaba.fastjson.JSON;
import feign.RequestTemplate;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClientExtend;

import java.lang.reflect.Method;

/**
 * description: Feign Request CircuitBreaker Extractor
 *
 * @author zhouxinlei
 * @since 2022-05-20 10:41:31
 */
@Slf4j
public class FeignRequestCircuitBreakerExtractor implements CircuitBreakerExtractor<RequestDataContext> {

    @Override
    public CircuitBreaker getCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry, Request<RequestDataContext> request, String host, int port) {
        RequestDataContext context = request.getContext();
        RequestTemplate requestTemplate = (RequestTemplate) context.getClientRequest().getAttributes()
                .get(FeignBlockingLoadBalancerClientExtend.REQUEST_TEMPLATE);
        Method method = requestTemplate.methodMetadata().method();
        String serviceInstanceMethodId = Resilience4jUtil.getServiceInstanceMethodId(host, port, method);
        FeignClient annotation = requestTemplate.methodMetadata().method().getDeclaringClass()
                .getAnnotation(FeignClient.class);
        //和 Retry 保持一致，使用 contextId，而不是微服务名称
        String contextId = annotation.contextId();
        // //每个服务实例具体方法一个resilience4j熔断记录器，在服务实例具体方法维度做熔断，所有这个服务的实例具体方法共享这个服务的resilience4j熔断配置
        CircuitBreaker circuitBreaker = Try.of(() -> circuitBreakerRegistry.circuitBreaker(serviceInstanceMethodId, contextId))
                .getOrElseGet(throwable -> {
                    log.error("CircuitBreaker exception：", throwable);
                    return circuitBreakerRegistry.circuitBreaker(serviceInstanceMethodId);
                });
        log.info("FeignRequestCircuitBreakerExtractor-getCircuitBreaker: {} -> {}", circuitBreaker.getName(), JSON.toJSONString(circuitBreaker.getMetrics()));
        return circuitBreaker;
    }
}
