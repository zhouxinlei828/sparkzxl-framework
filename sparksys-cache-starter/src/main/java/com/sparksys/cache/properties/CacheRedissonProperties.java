package com.sparksys.cache.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description：
 *
 * @author zhouxinlei
 * @date 2020/6/10 0010
 */

@Data
@ConfigurationProperties(prefix = CacheRedissonProperties.PREFIX)
public class CacheRedissonProperties {

    public static final String PREFIX = "cache.redisson";

    private boolean enable = Boolean.FALSE;

    private int timeout;

    private String address;

    private String password;

    private int connectionPoolSize;

    private int connectionMinimumIdleSize;

    private int slaveConnectionPoolSize;

    private int masterConnectionPoolSize;

    private String[] sentinelAddresses;

    private String masterName;

}
