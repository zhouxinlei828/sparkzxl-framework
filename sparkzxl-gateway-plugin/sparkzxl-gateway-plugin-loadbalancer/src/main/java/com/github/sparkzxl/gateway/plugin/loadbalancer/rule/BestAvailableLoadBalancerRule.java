package com.github.sparkzxl.gateway.plugin.loadbalancer.rule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2022-01-11 14:45:09
 */
@Slf4j
public class BestAvailableLoadBalancerRule implements ILoadBalancerRule {


    @Override
    public ServiceInstance chooseInstance(String serviceId, ServerHttpRequest request) {
        return null;
    }

    @Override
    public String name() {
        return null;
    }
}
