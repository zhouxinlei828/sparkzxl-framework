package com.github.sparkzxl.gateway.rule;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

/**
 * description: 负载均衡 服务类
 *
 * @author zhouxinlei
 * @date 2021-10-22 20:57:13
 */
public interface IReactorServiceInstanceLoadBalancer {

    /**
     * 根据serviceId 筛选可用服务
     *
     * @param serviceId 服务ID
     * @param request   请求
     * @return ServiceInstance
     */
    Mono<Response<ServiceInstance>> choose(String serviceId, ServerHttpRequest request);

    /**
     * 默认选择服务实例
     *
     * @param request request
     * @return Mono<Response < ServiceInstance>>
     */
    default Mono<Response<ServiceInstance>> choose(ServerHttpRequest request) {
        return Mono.just(new EmptyResponse());
    }
}
