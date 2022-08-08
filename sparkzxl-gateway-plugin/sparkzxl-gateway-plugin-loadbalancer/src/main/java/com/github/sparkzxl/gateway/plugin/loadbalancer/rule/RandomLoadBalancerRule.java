package com.github.sparkzxl.gateway.plugin.loadbalancer.rule;

import com.github.sparkzxl.gateway.plugin.common.constant.enums.RuleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * description: 随机负载均衡规则
 *
 * @author zhouxinlei
 * @since 2022-01-09 18:47:26
 */
@RequiredArgsConstructor
public class RandomLoadBalancerRule implements ILoadBalancerRule {

    private final DiscoveryClient discoveryClient;

    @Override
    public ServiceInstance chooseInstance(String serviceId, ServerHttpRequest request) {
        ServiceInstance serviceInstance;
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceId);
        int serviceInstanceCount = serviceInstanceList.size();
        if (serviceInstanceCount == 0) {
            return null;
        }
        int index = chooseRandomInt(serviceInstanceCount);
        serviceInstance = serviceInstanceList.get(index);
        return serviceInstance;
    }

    protected int chooseRandomInt(int serverCount) {
        return ThreadLocalRandom.current().nextInt(serverCount);
    }

    @Override
    public String name() {
        return RuleEnum.RANDOM.getName();
    }
}
