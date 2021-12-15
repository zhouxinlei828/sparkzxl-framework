package com.github.sparkzxl.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

/**
 * description: 网关插件属性配置
 *
 * @author zhoux
 */
@Slf4j
@Getter
@Setter
@ToString
public class GatewayPluginProperties implements InitializingBean {

    public static final String GATEWAY_PLUGIN_PROPERTIES_PREFIX = "spring.cloud.gateway.plugin.config";
    /**
     * Enable Or Disable Read Request Data 。 If true, all request body will cached
     */
    private boolean readRequestData = false;
    /**
     * Enable Or Disable Read Response Data
     */
    private boolean readResponseData = false;
    /**
     * Enable Or Disable Log Request Detail
     */
    private boolean logRequest = false;
    /**
     * Enable Or Disable Global Exception Json Handler
     */
    private boolean exceptionJsonHandler = false;
    /**
     * Enable Or Disable Dynamic Route
     */
    private boolean enableDynamicRoute = false;

    @Override
    public void afterPropertiesSet() {
    }
}
