package com.github.sparkzxl.gateway.annotation;

import com.github.sparkzxl.gateway.config.GatewayPluginConfigurationSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * description: 开启网关插件
 *
 * @author zhoux
 * @date 2021-10-23 16:46:42
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(GatewayPluginConfigurationSelector.class)
public @interface EnableGatewayPlugin {

}
