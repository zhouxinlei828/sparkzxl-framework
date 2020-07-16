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
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

    private Redisson redisson;


}
