package com.github.sparkzxl.distributed.cloud.config;

import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.distributed.cloud.loadbalancer.GrayscaleVersionServiceInstanceListSupplier;
import com.github.sparkzxl.distributed.cloud.loadbalancer.LoadBalancerVersionConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.DiscoveryClientServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * description: 灰度负载均衡配置
 *
 * @author zhouxinlei
 * @since 2022-07-12 18:50:58
 */
@Configuration(proxyBeanMethods = false)
public class GrayscaleLoadBalancerConfig {

    @Bean
    @ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
    public ServiceInstanceListSupplier serviceInstanceListSupplier(
            DiscoveryClient discoveryClient,
            Environment env,
            ConfigurableApplicationContext context,
            LoadBalancerVersionConfig versionConfig
    ) {
        ObjectProvider<LoadBalancerCacheManager> cacheManagerProvider = context
                //获取缓存管理器（这里其实就是 Caffeine），不能直接注入，会有找不到 Bean 的问题，因为加载顺序不可控
                .getBeanProvider(LoadBalancerCacheManager.class);
        //这里的流程就是，首先通过 DiscoveryClientServiceInstanceListSupplier 使用 discoveryClient 获取对应微服务的实例列表
        //然后通过我们自定义的 SameZoneOnlyServiceInstanceListSupplier 进行筛选
        //最后通过 CachingServiceInstanceListSupplier 将结果缓存起来
        return  //使用框架内置的 CachingServiceInstanceListSupplier 开启服务实例缓存，缓存需要在最外层，即缓存经过前面所有的 Supplier 筛选后的结果
                new CachingServiceInstanceListSupplier(
                        //使用我们自定义的 SameZoneOnlyServiceInstanceListSupplier，只能返回同一个 zone 的服务实例
                        new GrayscaleVersionServiceInstanceListSupplier(
                                //使用框架内置的 DiscoveryClientServiceInstanceListSupplier，通过 discoveryClient 的服务发现获取初始实例列表
                                new DiscoveryClientServiceInstanceListSupplier(discoveryClient, env),
                                versionConfig
                        ), Objects.requireNonNull(cacheManagerProvider.getIfAvailable())
                );
    }

    @Bean
    @ConditionalOnMissingClass("org.springframework.web.servlet.DispatcherServlet")
    @ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
    public ServiceInstanceListSupplier serviceInstanceListSupplierReactive(
            ReactiveDiscoveryClient reactiveDiscoveryClient,
            Environment env,
            ConfigurableApplicationContext context,
            LoadBalancerVersionConfig versionConfig
    ) {
        //获取缓存管理器（这里其实就是 Caffeine），不能直接注入，会有找不到 Bean 的问题，因为加载顺序不可控
        ObjectProvider<LoadBalancerCacheManager> cacheManagerProvider = context.getBeanProvider(LoadBalancerCacheManager.class);

        //这里的流程就是，首先通过 DiscoveryClientServiceInstanceListSupplier 使用 discoveryClient 获取对应微服务的实例列表
        //然后通过我们自定义的 SameZoneOnlyServiceInstanceListSupplier 进行筛选
        //最后通过 CachingServiceInstanceListSupplier 将结果缓存起来
        return  //使用框架内置的 CachingServiceInstanceListSupplier 开启服务实例缓存，缓存需要在最外层，即缓存经过前面所有的 Supplier 筛选后的结果
                new CachingServiceInstanceListSupplier(
                        //使用我们自定义的 SameZoneOnlyServiceInstanceListSupplier，只能返回同一个 zone 的服务实例
                        new GrayscaleVersionServiceInstanceListSupplier(
                                //使用框架内置的 DiscoveryClientServiceInstanceListSupplier，通过 discoveryClient 的服务发现获取初始实例列表
                                new DiscoveryClientServiceInstanceListSupplier(reactiveDiscoveryClient, env),
                                versionConfig
                        ), Objects.requireNonNull(cacheManagerProvider.getIfAvailable())
                );
    }

    @Bean
    public LoadBalancerVersionConfig loadBalancerVersionConfig() {
        return new LoadBalancerVersionConfig(RequestLocalContextHolder::getVersion);
    }

}
