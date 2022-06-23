package com.github.sparkzxl.gateway.plugin.properties;

import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 * description: Logging Properties
 *
 * @author zhouxinlei
 * @since 2021-12-23 09:53
 */
@Data
public class LoggingProperties {

    public static final String PREFIX = "spring.cloud.gateway.plugin.logging";

    private boolean enabled = false;
    private boolean all = false;

    /**
     * Enable Or Disable Read Request Data 。 If true, all request body will cached
     */
    private boolean readRequestData;
    /**
     * Enable Or Disable Read Response Data
     */
    private boolean readResponseData;

    private List<String> paths;

    public boolean match(String path) {
        return Optional.ofNullable(paths)
                .map(s -> s.contains(path))
                .orElse(true);
    }
}
