package com.github.sparkzxl.gateway.plugin.dubbo.config;

import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.gateway.common.constant.GatewayConstant;
import com.github.sparkzxl.gateway.common.entity.MetaData;
import com.github.sparkzxl.gateway.plugin.dubbo.constant.DubboConstant;
import com.github.sparkzxl.gateway.support.GatewayException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * description: The type Application config cache.
 *
 * @author zhouxinlei
 * @since 2022-08-12 15:06:44
 */
public class ApacheDubboConfigCache extends DubboConfigCache {

    private static final Logger logger = LoggerFactory.getLogger(ApacheDubboConfigCache.class);
    private final LoadingCache<String, ReferenceConfig<GenericService>> cache = CacheBuilder.newBuilder()
            .maximumSize(GatewayConstant.CACHE_MAX_COUNT)
            .removalListener((RemovalListener<Object, ReferenceConfig<GenericService>>) notification -> {
                ReferenceConfig<GenericService> config = notification.getValue();
                if (Objects.nonNull(config)) {
                    // After the configuration change, Dubbo destroys the instance, but does not empty it. If it is not handled,
                    // it will get NULL when reinitializing and cause a NULL pointer problem.
                    config.destroy();
                }
            })
            .build(new CacheLoader<String, ReferenceConfig<GenericService>>() {
                @Override
                @Nonnull
                public ReferenceConfig<GenericService> load(@Nonnull final String key) {
                    return new ReferenceConfig<>();
                }
            });
    private ApplicationConfig applicationConfig;
    private RegistryConfig registryConfig;
    private ConsumerConfig consumerConfig;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ApacheDubboConfigCache getInstance() {
        return ApplicationConfigCacheInstance.INSTANCE;
    }

    /**
     * Init
     *
     * @param dubboRegisterConfig the dubbo register config
     */
    public void init(final DubboRegisterConfig dubboRegisterConfig) {
        String applicationName = StringUtils.isEmpty(SpringContextUtils.getApplicationName()) ? "spring-gateway-proxy"
                : SpringContextUtils.getApplicationName();
        if (Objects.isNull(applicationConfig)) {
            applicationConfig = new ApplicationConfig(applicationName);
            applicationConfig.setRegisterMode("instance");
        }
        if (needUpdateRegistryConfig(dubboRegisterConfig)) {
            RegistryConfig registryConfigTemp = new RegistryConfig();
            registryConfigTemp.setProtocol(dubboRegisterConfig.getProtocol());
            registryConfigTemp.setId(applicationName);
            registryConfigTemp.setRegister(false);
            registryConfigTemp.setAddress(dubboRegisterConfig.getAddress());
            Optional.ofNullable(dubboRegisterConfig.getGroup()).ifPresent(registryConfigTemp::setGroup);
            Map<String, String> parameters = dubboRegisterConfig.getParameters();
            if (MapUtils.isNotEmpty(parameters)) {
                registryConfigTemp.setParameters(parameters);
            }
            registryConfig = registryConfigTemp;
        }
        if (Objects.isNull(consumerConfig)) {
            consumerConfig = new ConsumerConfig();
            consumerConfig.refresh();
            Optional.ofNullable(dubboRegisterConfig.getThreadPool()).ifPresent(consumerConfig::setThreadpool);
            Optional.ofNullable(dubboRegisterConfig.getCoreThreads()).ifPresent(consumerConfig::setCorethreads);
            Optional.ofNullable(dubboRegisterConfig.getThreads()).ifPresent(consumerConfig::setThreads);
            Optional.ofNullable(dubboRegisterConfig.getQueues()).ifPresent(consumerConfig::setQueues);
        }
    }


    private boolean needUpdateRegistryConfig(final DubboRegisterConfig dubboRegisterConfig) {
        if (Objects.isNull(registryConfig)) {
            return true;
        }
        return !Objects.equals(dubboRegisterConfig.getProtocol(), registryConfig.getProtocol())
                || !Objects.equals(dubboRegisterConfig.getAddress(), registryConfig.getAddress());
    }

    /**
     * Init ref reference config.
     *
     * @param metaData the meta data
     * @return the reference config
     */
    public ReferenceConfig<GenericService> initRef(final MetaData metaData) {
        try {
            ReferenceConfig<GenericService> referenceConfig = cache.get(metaData.getPath());
            if (StringUtils.isNoneBlank(referenceConfig.getInterface())) {
                return referenceConfig;
            }
        } catch (ExecutionException e) {
            logger.error("init dubbo ref exception", e);
        }
        return build(metaData);
    }


    /**
     * Build reference config.
     *
     * @param metaData the meta data
     * @return the reference config
     */
    public ReferenceConfig<GenericService> build(final MetaData metaData) {
        if (Objects.isNull(applicationConfig) || Objects.isNull(registryConfig)) {
            return new ReferenceConfig<>();
        }
        ReferenceConfig<GenericService> reference = buildReference(metaData);
        try {
            Object obj = reference.get();
            if (Objects.nonNull(obj)) {
                logger.info("build init apache dubbo reference success there meteData is :{}", metaData);
                cache.put(metaData.getNamespace() + ":" + metaData.getPath(), reference);
            }
        } catch (Exception e) {
            logger.error("init apache dubbo reference exception", e);
        }
        return reference;
    }

    /**
     * buildReference param.
     *
     * @param metaData metaData
     * @return the reference config
     */
    @SuppressWarnings(value = "all")
    private ReferenceConfig<GenericService> buildReference(final MetaData metaData) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setInterface(metaData.getServiceName());
        reference.setGeneric("true");
        reference.setProtocol("dubbo");
        reference.setAsync(true);
        reference.setCheck(false);
        reference.setApplication(applicationConfig);
        reference.setConsumer(consumerConfig);
        String rpcExt = metaData.getRpcExt();
        DubboParam dubboParam = parserToDubboParam(rpcExt);
        if (Objects.nonNull(dubboParam)) {
            if (StringUtils.isNoneBlank(dubboParam.getVersion())) {
                reference.setVersion(dubboParam.getVersion());
            }
            if (StringUtils.isNoneBlank(dubboParam.getGroup())) {
                reference.setGroup(dubboParam.getGroup());
            }
            if (StringUtils.isNoneBlank(dubboParam.getUrl())) {
                reference.setUrl(dubboParam.getUrl());
            }
            if (StringUtils.isNoneBlank(dubboParam.getCluster())) {
                reference.setCluster(dubboParam.getCluster());
            }
            Optional.ofNullable(dubboParam.getTimeout()).ifPresent(reference::setTimeout);
            Optional.ofNullable(dubboParam.getRetries()).ifPresent(reference::setRetries);
            Optional.ofNullable(dubboParam.getSent()).ifPresent(reference::setSent);
        }
        RegistryConfig registryConfigTemp = new RegistryConfig();
        String namespace = metaData.getNamespace();
        BeanUtils.copyProperties(registryConfig, registryConfigTemp, "address");
        if (StringUtils.isNotBlank(namespace)) {
            if (!registryConfig.getAddress().contains(DubboConstant.NAMESPACE)) {
                String newAddress = registryConfig.getAddress() + "?" + DubboConstant.NAMESPACE + "=" + namespace;
                registryConfigTemp.setAddress(newAddress);
            } else {
                String newAddress =
                        registryConfig.getAddress().substring(0, registryConfig.getAddress().indexOf(DubboConstant.NAMESPACE) + 1)
                                + DubboConstant.NAMESPACE + "=" + namespace;
                registryConfigTemp.setAddress(newAddress);
            }
        }
        reference.setRegistry(registryConfigTemp);
        return reference;
    }

    /**
     * Get reference config.
     *
     * @param path the path
     * @return the reference config
     */
    public ReferenceConfig<GenericService> get(final String path) {
        try {
            return cache.get(path);
        } catch (ExecutionException e) {
            throw new GatewayException("500", "Not found reference config.");
        }
    }

    /**
     * Invalidate.
     *
     * @param path the path
     */
    public void invalidate(final String path) {
        cache.invalidate(path);
    }

    /**
     * Invalidate all.
     */
    public void invalidateAll() {
        cache.invalidateAll();
    }

    /**
     * The type Application config cache instance.
     */
    static final class ApplicationConfigCacheInstance {

        /**
         * The Instance.
         */
        static final ApacheDubboConfigCache INSTANCE = new ApacheDubboConfigCache();

        private ApplicationConfigCacheInstance() {

        }
    }


}
