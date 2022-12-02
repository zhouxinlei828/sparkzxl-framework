package com.github.sparkzxl.feign.resilience4j.autoconfigure;

import com.github.sparkzxl.feign.resilience4j.CircuitBreakerExtractor;
import com.github.sparkzxl.feign.resilience4j.FeignRequestCircuitBreakerExtractor;
import com.github.sparkzxl.feign.resilience4j.WebClientRequestCircuitBreakerExtractor;
import com.github.sparkzxl.feign.resilience4j.loadbalancer.SameZoneOnlyServiceInstanceListSupplier;
import com.github.sparkzxl.feign.resilience4j.loadbalancer.TracedCircuitBreakerRoundRobinLoadBalancer;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.config.LoadBalancerZoneConfig;
import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.DiscoveryClientServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * description: 默认负载均衡配置
 *
 * @author zhouxinlei
 * @since 2022-12-01 11:26:18
 */
@Configuration(proxyBeanMethods = false)
public class DefaultLoadBalancerConfiguration {

    /**
     * {@link @ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")}代表有spring-mvc 依赖
     * <p>
     * 这里的流程就是，首先通过 DiscoveryClientServiceInstanceListSupplier 使用 discoveryClient 获取对应微服务的实例列表
     * 然后通过我们自定义的 SameZoneOnlyServiceInstanceListSupplier 进行筛选
     * 最后通过 CachingServiceInstanceListSupplier 将结果缓存起来
     * </p>
     *
     * @param discoveryClient 发现客户端
     * @param env             上下文环境
     * @param context         应用上下文
     * @param zoneConfig      负载均衡zoneConfig
     * @return ServiceInstanceListSupplier
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
    public ServiceInstanceListSupplier serviceInstanceListSupplier(
            DiscoveryClient discoveryClient,
            Environment env,
            ConfigurableApplicationContext context,
            LoadBalancerZoneConfig zoneConfig
    ) {
        ObjectProvider<LoadBalancerCacheManager> cacheManagerProvider = context
                //获取缓存管理器（这里其实就是 Caffeine），不能直接注入，会有找不到 Bean 的问题，因为加载顺序不可控
                .getBeanProvider(LoadBalancerCacheManager.class);
        //最后通过 CachingServiceInstanceListSupplier 将结果缓存起来
        return  //使用框架内置的 CachingServiceInstanceListSupplier 开启服务实例缓存，缓存需要在最外层，即缓存经过前面所有的 Supplier 筛选后的结果
                new CachingServiceInstanceListSupplier(
                        //使用我们自定义的 SameZoneOnlyServiceInstanceListSupplier，只能返回同一个 zone 的服务实例
                        new SameZoneOnlyServiceInstanceListSupplier(
                                //使用框架内置的 DiscoveryClientServiceInstanceListSupplier，通过 discoveryClient 的服务发现获取初始实例列表
                                new DiscoveryClientServiceInstanceListSupplier(discoveryClient, env),
                                zoneConfig
                        )
                        , Objects.requireNonNull(cacheManagerProvider.getIfAvailable())
                );
    }

    /**
     * {@link @ConditionalOnMissingClass("org.springframework.web.servlet.DispatcherServlet")}代表没有spring-mvc 依赖
     * {@link @ConditionalOnClass("org.springframework.web.reactive.DispatcherHandler")}代表spring-webflux 依赖
     * 对于只包含 spring-webflux 依赖我们使用异步 Discovery 客户端
     *
     * @param reactiveDiscoveryClient 对于只包含 spring-webflux 依赖我们使用异步 Discovery 客户端
     * @param env                     上下文环境
     * @param context                 应用上下文
     * @param zoneConfig              负载均衡zoneConfig
     * @return ServiceInstanceListSupplier
     */
    @Bean
    @ConditionalOnMissingClass("org.springframework.web.servlet.DispatcherServlet")
    @ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
    public ServiceInstanceListSupplier serviceInstanceListSupplierReactive(
            ReactiveDiscoveryClient reactiveDiscoveryClient,
            Environment env,
            ConfigurableApplicationContext context,
            LoadBalancerZoneConfig zoneConfig
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
                        new SameZoneOnlyServiceInstanceListSupplier(
                                //使用框架内置的 DiscoveryClientServiceInstanceListSupplier，通过 discoveryClient 的服务发现获取初始实例列表
                                new DiscoveryClientServiceInstanceListSupplier(reactiveDiscoveryClient, env),
                                zoneConfig
                        )
                        , cacheManagerProvider.getIfAvailable()
                );
    }


    /**
     * {@link @ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")}代表有spring-mvc 依赖
     *
     * @return CircuitBreakerExtractor
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
    public CircuitBreakerExtractor feignRequestCircuitBreakerExtractor() {
        return new FeignRequestCircuitBreakerExtractor();
    }

    /**
     * {@link @ConditionalOnMissingClass("org.springframework.web.servlet.DispatcherServlet")}代表没有spring-mvc 依赖
     * {@link @ConditionalOnClass("org.springframework.web.reactive.DispatcherHandler")}代表spring-webflux 依赖
     *
     * @return CircuitBreakerExtractor
     */
    @Bean
    @ConditionalOnMissingClass("org.springframework.web.servlet.DispatcherServlet")
    @ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
    public CircuitBreakerExtractor webClientCircuitBreakerExtractor() {
        return new WebClientRequestCircuitBreakerExtractor();
    }


    @Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
            Environment environment,
            ServiceInstanceListSupplier serviceInstanceListSupplier,
            CircuitBreakerExtractor circuitBreakerExtractor,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new TracedCircuitBreakerRoundRobinLoadBalancer(
                serviceInstanceListSupplier, name, circuitBreakerExtractor, circuitBreakerRegistry
        );
    }
}
