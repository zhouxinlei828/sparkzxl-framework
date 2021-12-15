package com.github.sparkzxl.gateway.config;

import com.github.sparkzxl.gateway.predicate.DynamicRoutePredicateHandlerMapping;
import com.github.sparkzxl.gateway.predicate.DynamicRouteSupportBeanFactoryPostProcessor;
import com.github.sparkzxl.gateway.properties.GatewayPluginProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 网关插件配置选择
 *
 * @author zhoux
 */
@Slf4j
public class GatewayPluginConfigurationSelector implements ImportSelector, EnvironmentAware {

    private Binder binder;

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        List<Class> configClassList = new ArrayList<>();
        configClassList.add(GatewayPluginConfig.class);
        configClassList.add(RequestResponseLogConfig.class);
        configClassList.add(GlobalExceptionJsonHandlerConfig.class);
        if (enableDynamicRoute()) {
            configClassList.add(DynamicRouteSupportBeanFactoryPostProcessor.class);
            configClassList.add(DynamicRouteConfiguration.class);
            configClassList.add(DynamicRoutePredicateHandlerMapping.class);
        }
        return configClassList.stream().map(Class::getName).toArray(String[]::new);
    }

    /**
     * according to properties settings to enable Dynamic Route Config
     *
     * @return true/false
     */
    private boolean enableDynamicRoute() {
        return binder
                .bind(GatewayPluginProperties.GATEWAY_PLUGIN_PROPERTIES_PREFIX + ".enable-dynamic-route", Bindable.of(Boolean.class))
                .orElse(Boolean.FALSE);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.binder = Binder.get(environment);
    }
}
