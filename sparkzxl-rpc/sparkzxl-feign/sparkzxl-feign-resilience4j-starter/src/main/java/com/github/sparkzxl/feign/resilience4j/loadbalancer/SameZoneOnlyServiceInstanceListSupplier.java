package com.github.sparkzxl.feign.resilience4j.loadbalancer;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.config.LoadBalancerZoneConfig;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ZonePreferenceServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

/**
 * description: 只返回与当前实例同一个 Zone 的服务实例，不同 zone 之间的服务不互相调用
 *
 * @author zhouxinlei
 * @since 2022-12-01 11:27:12
 */
public class SameZoneOnlyServiceInstanceListSupplier extends DelegatingServiceInstanceListSupplier {

    private final LoadBalancerZoneConfig zoneConfig;

    private String zone;

    public SameZoneOnlyServiceInstanceListSupplier(ServiceInstanceListSupplier delegate,
            LoadBalancerZoneConfig zoneConfig) {
        super(delegate);
        this.zoneConfig = zoneConfig;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return getDelegate().get().map(this::filteredByZone);
    }

    /**
     * @see ZonePreferenceServiceInstanceListSupplier 在没有相同zone实例的时候返回的是所有实例 我们这里为了实现不同 zone 之间不互相调用需要返回空列表
     */
    private List<ServiceInstance> filteredByZone(List<ServiceInstance> serviceInstances) {
        if (zone == null) {
            zone = zoneConfig.getZone();
        }
        if (zone != null) {
            List<ServiceInstance> filteredInstances = new ArrayList<>();
            for (ServiceInstance serviceInstance : serviceInstances) {
                String instanceZone = getZone(serviceInstance);
                if (zone.equalsIgnoreCase(instanceZone)) {
                    filteredInstances.add(serviceInstance);
                }
            }
            if (filteredInstances.size() > 0) {
                return filteredInstances;
            }
        }
        return serviceInstances;
    }

    private String getZone(ServiceInstance serviceInstance) {
        Map<String, String> metadata = serviceInstance.getMetadata();
        if (metadata != null) {
            String zone = "zone";
            return metadata.get(zone);
        }
        return null;
    }

}
