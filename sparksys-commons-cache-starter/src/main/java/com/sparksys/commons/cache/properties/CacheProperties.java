package com.sparksys.commons.cache.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * description：
 *
 * @author zhouxinlei
 * @date 2020/6/10 0010
 */
@ConfigurationProperties(prefix = "cache")
@Component
@Data
public class CacheProperties {

    private Redisson redisson;


}
