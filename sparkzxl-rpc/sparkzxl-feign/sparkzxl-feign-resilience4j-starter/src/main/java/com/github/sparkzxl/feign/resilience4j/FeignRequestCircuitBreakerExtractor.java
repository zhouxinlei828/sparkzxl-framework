package com.github.sparkzxl.feign.resilience4j;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.feign.resilience4j.utils.ServiceInstanceUtil;
import feign.RequestTemplate;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.ConfigurationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClientExtend;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-12-01 11:03:42
 */
@Slf4j
public class FeignRequestCircuitBreakerExtractor implements CircuitBreakerExtractor {

    @Override
    public CircuitBreaker getCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry, Request request, String host, int port) {
        RequestDataContext context = (RequestDataContext) request.getContext();
        RequestTemplate requestTemplate = (RequestTemplate) context.getClientRequest().getAttributes()
                .get(FeignBlockingLoadBalancerClientExtend.REQUEST_TEMPLATE);
        Method method = requestTemplate.methodMetadata().method();
        String serviceInstanceMethodId = ServiceInstanceUtil.getServiceInstanceMethodId(host, port, method);
        FeignClient annotation = requestTemplate.methodMetadata().method().getDeclaringClass()
                .getAnnotation(FeignClient.class);
        //和 Retry 保持一致，使用 contextId，而不是微服务名称
        String contextId = annotation.contextId();
        CircuitBreaker circuitBreaker;
        try {
            //每个服务实例具体方法一个resilience4j熔断记录器，在服务实例具体方法维度做熔断，所有这个服务的实例具体方法共享这个服务的resilience4j熔断配置
            circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceInstanceMethodId, contextId);
        } catch (ConfigurationNotFoundException e) {
            circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceInstanceMethodId);
        }
        log.info("FeignRequestCircuitBreakerExtractor-getCircuitBreaker: {} -> {}", circuitBreaker.getName(), JSON.toJSONString(circuitBreaker.getMetrics()));
        return circuitBreaker;
    }

    @Override
    public String getTraceId(Request request) {
        RequestDataContext context = (RequestDataContext) request.getContext();
        RequestTemplate requestTemplate = (RequestTemplate) context.getClientRequest().getAttributes()
                .get(FeignBlockingLoadBalancerClientExtend.REQUEST_TEMPLATE);
        List<String> headers = (List<String>) requestTemplate.headers().get(BaseContextConstants.TRACE_ID_HEADER);
        if (CollectionUtils.isEmpty(headers)) {
            return "";
        }
        return headers.get(0);
    }
}
