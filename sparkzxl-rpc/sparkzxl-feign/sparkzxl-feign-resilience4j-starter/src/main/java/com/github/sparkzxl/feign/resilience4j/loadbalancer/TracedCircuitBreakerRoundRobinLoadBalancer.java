package com.github.sparkzxl.feign.resilience4j.loadbalancer;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.sparkzxl.feign.resilience4j.CircuitBreakerExtractor;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

/**
 * description: 改进负载均衡算法
 * <p>
 * 思路： 针对每次请求，记录： 1.本次请求已经调用过哪些实例 -> 请求调用过的实例缓存 2.调用的实例，当前有多少请求在处理中 -> 实例运行请求数 3.调用的实例，最近请求错误率 -> 实例请求错误率
 * 4.随机将实例列表打乱，防止在以上三个指标都相同时，总是将请求发给同一个实例。 5.按照 当前请求没有调用过靠前 -> 错误率越小越靠前 的顺序排序 -> 实例运行请求数越小越靠前 6.取排好序之后的列表第一个实例作为本次负载均衡的实例
 * <p>
 *
 * @author zhouxinlei
 * @since 2022-12-01 10:10:16
 */
@Slf4j
@SuppressWarnings("ALL")
public class TracedCircuitBreakerRoundRobinLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    /**
     * 每次请求算上重试不会超过3分钟 对于超过3分钟的，这种请求肯定比较重，不应该重试
     */
    private final LoadingCache<String, AtomicInteger> positionCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            //随机初始值，防止每次都是从第一个开始调用
            .build(k -> new AtomicInteger(ThreadLocalRandom.current().nextInt(0, 1000)));
    private final LoadingCache<String, Set<String>> calledIpPrefixes = Caffeine.newBuilder()
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .build(k -> Sets.newConcurrentHashSet());
    private final LoadingCache<String, Set<String>> calledIps = Caffeine.newBuilder()
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .build(k -> Sets.newConcurrentHashSet());

    private ServiceInstanceListSupplier serviceInstanceListSupplier;
    private String serviceId;
    private CircuitBreakerRegistry circuitBreakerRegistry;
    private final CircuitBreakerExtractor circuitBreakerExtractor;

    public TracedCircuitBreakerRoundRobinLoadBalancer(
            ServiceInstanceListSupplier serviceInstanceListSupplier, String serviceId,
            CircuitBreakerExtractor circuitBreakerExtractor,
            CircuitBreakerRegistry circuitBreakerRegistry) {
        this.serviceInstanceListSupplier = serviceInstanceListSupplier;
        this.serviceId = serviceId;
        this.circuitBreakerExtractor = circuitBreakerExtractor;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    public void setServiceInstanceListSupplier(ServiceInstanceListSupplier serviceInstanceListSupplier) {
        this.serviceInstanceListSupplier = serviceInstanceListSupplier;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setCircuitBreakerRegistry(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }


    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        return serviceInstanceListSupplier.get().next()
                .map(serviceInstances -> getInstanceResponse(serviceInstances, request));
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> serviceInstances, Request request) {
        if (serviceInstances.isEmpty()) {
            log.warn("No servers available for service: " + this.serviceId);
            return new EmptyResponse();
        }
        serviceInstances = serviceInstances.stream().distinct().collect(Collectors.toList());
        Map<ServiceInstance, CircuitBreaker> serviceInstanceCircuitBreakerMap = serviceInstances.stream()
                .collect(Collectors.toMap(serviceInstance -> serviceInstance, v -> circuitBreakerExtractor
                        .getCircuitBreaker(circuitBreakerRegistry, request, v.getHost(), v.getPort())));
        String traceId = circuitBreakerExtractor.getTraceId(request);
        return getInstanceResponseByRoundRobin(traceId, serviceInstances, serviceInstanceCircuitBreakerMap);
    }

    public Response<ServiceInstance> getInstanceResponseByRoundRobin(String traceId, List<ServiceInstance> serviceInstances,
            Map<ServiceInstance, CircuitBreaker> serviceInstanceCircuitBreakerMap) {
        Collections.shuffle(serviceInstances);
        //需要先将所有参数缓存起来，否则 comparator 会调用多次，并且可能在排序过程中参数发生改变
        Map<ServiceInstance, Integer> used = Maps.newHashMap();
        serviceInstances = serviceInstances.stream().sorted(
                Comparator
                        //之前已经调用过的 ip，这里排后面
                        .<ServiceInstance>comparingInt(serviceInstance ->
                                used.computeIfAbsent(serviceInstance, k -> Objects.requireNonNull(calledIps.get(traceId))
                                        .stream().anyMatch(prefix -> serviceInstance.getHost().equalsIgnoreCase(prefix)) ? 1 : 0))
                        //之前已经调用过的网段，这里排后面
                        .thenComparingInt(serviceInstance ->
                                used.computeIfAbsent(serviceInstance, k -> Objects.requireNonNull(calledIpPrefixes.get(traceId))
                                        .stream().anyMatch(prefix -> serviceInstance.getHost().contains(prefix)) ? 1 : 0))
                        //当前断路器没有打开的优先
                        .thenComparingInt(serviceInstance -> serviceInstanceCircuitBreakerMap.get(serviceInstance).getState().getOrder())
                        //当前错误率最少的
                        .thenComparingDouble(
                                serviceInstance -> serviceInstanceCircuitBreakerMap.get(serviceInstance).getMetrics().getFailureRate())
                        //当前负载请求最少的
                        .thenComparingLong(serviceInstance -> serviceInstanceCircuitBreakerMap.get(serviceInstance).getMetrics()
                                .getNumberOfBufferedCalls())
        ).collect(Collectors.toList());
        if (serviceInstances.isEmpty()) {
            log.warn("No servers available for service: " + this.serviceId);
            return new EmptyResponse();
        }
        ServiceInstance serviceInstance = serviceInstances.get(0);
        //记录本次返回的网段
        calledIpPrefixes.get(traceId).add(serviceInstance.getHost().substring(0, serviceInstance.getHost().lastIndexOf(".")));
        calledIps.get(traceId).add(serviceInstance.getHost());
        //目前记录这个只为了兼容之前的单元测试（调用次数测试）
        positionCache.get(traceId).getAndIncrement();
        return new DefaultResponse(serviceInstance);
    }

}
