package com.github.sparkzxl.gateway.plugin.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Gateway Plugin Configuration Selector
 *
 * @author chenggang
 * @since 2019/01/29
 */
@Slf4j
public class GatewayPluginConfigurationSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        List<Class> configClassList = new ArrayList<>();
        configClassList.add(ExceptionHandlerStrategyAutoConfig.class);
        configClassList.add(GlobalExceptionJsonHandlerConfig.class);
        return configClassList.stream().map(Class::getName).toArray(String[]::new);
    }
}
