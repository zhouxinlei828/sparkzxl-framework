package com.github.sparkzxl.gateway.rule;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * description: 灰度路由 服务类
 *
 * @author zhouxinlei
 * @date 2021-10-22 20:57:13
 */
public interface RouteLoadBalancer {

    /**
     * 根据serviceId 筛选可用服务
     *
     * @param serviceId 服务ID
     * @param request   请求
     * @return ServiceInstance
     */
    ServiceInstance choose(String serviceId, ServerHttpRequest request);

}
