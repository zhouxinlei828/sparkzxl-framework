package com.github.sparkzxl.distributed.cloud.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

@ConditionalOnProperty(value = "spring.cloud.loadbalancer.grayscale.enabled", havingValue = "true", matchIfMissing = true)
@LoadBalancerClients(defaultConfiguration = GrayscaleLoadBalancerConfig.class)
public class GrayscaleAutoConfig {

}
