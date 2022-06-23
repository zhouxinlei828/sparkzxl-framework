package com.github.sparkzxl.gateway.plugin.autoconfigure.annotation;

import com.github.sparkzxl.gateway.plugin.autoconfigure.GatewayPluginConfigurationSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * description: Enable Gateway Plugin
 *
 * @author zhouxinlei
 * @since 2022-01-11 12:52:12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(GatewayPluginConfigurationSelector.class)
public @interface EnableExceptionJsonHandler {

}
