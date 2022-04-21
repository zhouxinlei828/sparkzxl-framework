package com.github.sparkzxl.gateway.plugin.loadbalancer.service;

import com.github.sparkzxl.gateway.plugin.loadbalancer.rule.ILoadBalancerRule;
import com.github.sparkzxl.gateway.plugin.properties.ReactiveLoadBalancerProperties;
import com.github.sparkzxl.gateway.plugin.support.GatewayException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * description: 灰度版本负载均衡
 *
 * @author zhoux
 * @date 2021-10-23 16:45:51
 */
@Slf4j
public class ReactorServiceInstanceLoadBalancer implements IReactorServiceInstanceLoadBalancer {

    private final ReactiveLoadBalancerProperties reactiveLoadBalancerProperties;
    private final Map<String, ILoadBalancerRule> loadBalancerRuleMap;

    public ReactorServiceInstanceLoadBalancer(List<ILoadBalancerRule> loadBalancerRuleList, ReactiveLoadBalancerProperties reactiveLoadBalancerProperties) {
        this.reactiveLoadBalancerProperties = reactiveLoadBalancerProperties;
        loadBalancerRuleMap = Maps.newHashMap();
        loadBalancerRuleList.forEach(rule -> loadBalancerRuleMap.put(rule.name(), rule));
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(String serviceId, ServerHttpRequest request) {
        ILoadBalancerRule loadBalancerRule = loadBalancerRuleMap.get(reactiveLoadBalancerProperties.getRule());
        if (ObjectUtils.isEmpty(loadBalancerRule)) {
            throw new GatewayException("500", "未找到负载均衡算法");
        }
        ServiceInstance serviceInstance = loadBalancerRule.chooseInstance(serviceId, request);
        return Mono.just(new DefaultResponse(serviceInstance));
    }
}
