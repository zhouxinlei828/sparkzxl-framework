package com.github.sparkzxl.gateway.loadbalancer;

import com.github.sparkzxl.gateway.properties.ReactiveLoadBalancerProperties;
import com.github.sparkzxl.gateway.rule.ILoadBalancerRule;
import com.github.sparkzxl.gateway.support.GatewayException;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * description: 灰度版本负载均衡
 *
 * @author zhoux
 * @date 2021-10-23 16:45:51
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ReactorServiceInstanceLoadBalancer implements IReactorServiceInstanceLoadBalancer {

    private Map<String, ILoadBalancerRule> loadBalancerRuleMap;
    private final ReactiveLoadBalancerProperties reactiveLoadBalancerProperties;

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
