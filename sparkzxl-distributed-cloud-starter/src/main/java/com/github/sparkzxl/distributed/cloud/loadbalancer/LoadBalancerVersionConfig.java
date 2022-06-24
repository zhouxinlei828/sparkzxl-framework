package com.github.sparkzxl.distributed.cloud.loadbalancer;

import java.util.function.Supplier;

/**
 * description: 负载均衡版本配置
 *
 * @author zhouxinlei
 * @since 2022-06-24 11:22:52
 */
public class LoadBalancerVersionConfig {

    private final Supplier<String> version;

    public LoadBalancerVersionConfig(Supplier<String> version) {
        this.version = version;
    }

    public String getVersion() {
        return version.get();
    }
}
