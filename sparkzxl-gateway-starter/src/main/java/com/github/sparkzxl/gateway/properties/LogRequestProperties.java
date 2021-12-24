package com.github.sparkzxl.gateway.properties;

import lombok.Data;

import java.util.*;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-12-23 09:53
 */
@Data
public class LogRequestProperties {

    public static final String GATEWAY_PLUGIN_LOG_PROPERTIES_PREFIX = "spring.cloud.gateway.plugin.logging";

    private boolean enabled = false;
    private boolean all = false;

    /**
     * Enable Or Disable Read Request Data 。 If true, all request body will cached
     */
    private boolean readRequestData = false;
    /**
     * Enable Or Disable Read Response Data
     */
    private boolean readResponseData = false;

    /**
     * HOST 下精确配置路径
     */
    private Map<String, Set<String>> routePathSetMap = Collections.emptyMap();

    public boolean match(String routeId, String path) {
        return Optional.ofNullable(routePathSetMap.get(routeId))
                .map(s -> s.contains(path))
                .orElse(false);
    }
}
