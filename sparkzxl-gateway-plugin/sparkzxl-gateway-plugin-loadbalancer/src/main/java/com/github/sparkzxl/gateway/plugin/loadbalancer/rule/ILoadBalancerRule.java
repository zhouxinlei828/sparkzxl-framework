package com.github.sparkzxl.gateway.plugin.loadbalancer.rule;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * description: 负载均衡规则
 *
 * @author zhouxinlei
 * @since 2022-01-09 18:45:27
 */
public interface ILoadBalancerRule {

    /**
     * 根据serviceId 筛选可用服务
     *
     * @param serviceId 服务ID
     * @param request   请求
     * @return ServiceInstance
     */
    ServiceInstance chooseInstance(String serviceId, ServerHttpRequest request);

    /**
     * 规则名称
     *
     * @return String
     */
    String name();
}
