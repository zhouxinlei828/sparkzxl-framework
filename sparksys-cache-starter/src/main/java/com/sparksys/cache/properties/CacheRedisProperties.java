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
@ConfigurationProperties(prefix = CacheRedisProperties.PREFIX)
public class CacheRedisProperties {

    public static final String PREFIX = "cache.redis";

    private boolean enable = Boolean.FALSE;

}
