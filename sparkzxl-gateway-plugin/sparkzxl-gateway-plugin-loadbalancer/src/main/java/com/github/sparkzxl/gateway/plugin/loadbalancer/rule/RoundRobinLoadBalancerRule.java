package com.github.sparkzxl.gateway.plugin.loadbalancer.rule;

import com.github.sparkzxl.gateway.plugin.common.constant.enums.RuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description: 轮询方式轮询选择服务实例
 *
 * @author zhouxinlei
 * @since 2022-01-11 13:56:27
 */
@Slf4j
public class RoundRobinLoadBalancerRule implements ILoadBalancerRule {

    private final AtomicInteger nextServerCyclicCounter;
    private final DiscoveryClient discoveryClient;


    public RoundRobinLoadBalancerRule(DiscoveryClient discoveryClient) {
        nextServerCyclicCounter = new AtomicInteger(0);
        this.discoveryClient = discoveryClient;
    }

    @Override
    public ServiceInstance chooseInstance(String serviceId, ServerHttpRequest request) {
        ServiceInstance serviceInstance = null;
        int count = 0;
        while (serviceInstance == null && count++ < 10) {
            List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceId);
            int serverCount = serviceInstanceList.size();
            if (serverCount == 0) {
                log.warn("No up service instance available from load balancer");
                return null;
            }
            int nextServerIndex = incrementAndGetModulo(serverCount);
            serviceInstance = serviceInstanceList.get(nextServerIndex);
            if (serviceInstance == null) {
                /* Transient. */
                Thread.yield();
                continue;
            }
            return serviceInstance;
        }

        return null;
    }


    private int incrementAndGetModulo(int modulo) {
        for (; ; ) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    @Override
    public String name() {
        return RuleEnum.ROUND_ROBIN.getName();
    }
}
