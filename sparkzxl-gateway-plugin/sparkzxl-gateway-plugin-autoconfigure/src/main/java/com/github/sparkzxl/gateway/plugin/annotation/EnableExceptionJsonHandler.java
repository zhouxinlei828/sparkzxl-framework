package com.github.sparkzxl.gateway.plugin.annotation;

import com.github.sparkzxl.gateway.plugin.autoconfigure.GatewayPluginConfigurationSelector;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

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
