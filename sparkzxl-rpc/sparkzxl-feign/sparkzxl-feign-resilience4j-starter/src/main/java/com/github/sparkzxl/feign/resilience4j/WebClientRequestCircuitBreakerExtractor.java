package com.github.sparkzxl.feign.resilience4j;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.github.sparkzxl.core.constant.BaseContextConstants;
import com.github.sparkzxl.feign.resilience4j.utils.ServiceInstanceUtil;
import com.google.common.collect.Lists;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.ConfigurationNotFoundException;
import java.text.MessageFormat;
import java.util.List;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestAdapter;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-12-01 14:58:42
 */
public class WebClientRequestCircuitBreakerExtractor implements CircuitBreakerExtractor {

    @Override
    public CircuitBreaker getCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry, Request request, String host, int port) {
        RequestDataContext context = Convert.convert(RequestDataContext.class, request.getContext());
        String serviceName;
        String serviceInstanceMethodId;
        if (request instanceof LoadBalancerRequestAdapter) {
            serviceName = MessageFormat.format("{0}:{1}", host, port);
            serviceInstanceMethodId = MessageFormat.format("{0}:{1}:{2}", host, port, context.getHint());
        } else {
            RequestData requestData = context.getClientRequest();
            String path = requestData.getUrl().getPath();
            //这里 host 就是微服务名称，对于 webClient 使用微服务名称配置的 resilience4j 相关的元素
            serviceName = requestData.getUrl().getHost();
            serviceInstanceMethodId = ServiceInstanceUtil.getServiceInstanceMethodId(host, port, path);
        }
        CircuitBreaker circuitBreaker;
        try {
            //每个服务实例具体方法一个resilience4j熔断记录器，在服务实例具体方法维度做熔断，所有这个服务的实例具体方法共享这个服务的resilience4j熔断配置
            circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceInstanceMethodId, serviceName);
        } catch (ConfigurationNotFoundException e) {
            circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceInstanceMethodId);
        }
        return circuitBreaker;
    }

    @Override
    public String getTraceId(Request request) {
        RequestDataContext context = Convert.convert(RequestDataContext.class, request.getContext());
        RequestData requestData = context.getClientRequest();
        List<String> headers = ObjectUtils.isEmpty(requestData) ? Lists.newArrayList()
                : requestData.getHeaders().get(BaseContextConstants.TRACE_ID_HEADER);
        if (CollectionUtils.isEmpty(headers)) {
            return IdUtil.fastSimpleUUID();
        }
        return headers.get(0);
    }
}
