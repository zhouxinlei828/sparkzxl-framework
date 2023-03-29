package com.github.sparkzxl.distributed.cloud.loadbalancer;

import com.google.common.collect.Lists;
import io.seata.common.util.StringUtils;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

/**
 * description: 只返回相同【version】的服务实例，不同【version】之间的服务不互相调用
 *
 * @author zhouxinlei
 * @since 2022-06-24 11:20:09
 */
public class GrayscaleVersionServiceInstanceListSupplier extends DelegatingServiceInstanceListSupplier {

    private static final String VERSION = "version";
    private final LoadBalancerVersionConfig versionConfig;

    public GrayscaleVersionServiceInstanceListSupplier(ServiceInstanceListSupplier delegate,
            LoadBalancerVersionConfig versionConfig) {
        super(delegate);
        this.versionConfig = versionConfig;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return getDelegate().get().map(this::filteredByVersion);
    }

    private List<ServiceInstance> filteredByVersion(List<ServiceInstance> serviceInstances) {
        String version = versionConfig.getVersion();
        if (StringUtils.isNotEmpty(version)) {
            List<ServiceInstance> filteredInstances = Lists.newArrayList();
            for (ServiceInstance serviceInstance : serviceInstances) {
                String instanceVersion = getZone(serviceInstance);
                if (version.equalsIgnoreCase(instanceVersion)) {
                    filteredInstances.add(serviceInstance);
                }
            }
            return filteredInstances;
        }
        return serviceInstances;
    }

    private String getZone(ServiceInstance serviceInstance) {
        Map<String, String> metadata = serviceInstance.getMetadata();
        if (metadata != null) {
            return metadata.get(VERSION);
        }
        return null;
    }
}
