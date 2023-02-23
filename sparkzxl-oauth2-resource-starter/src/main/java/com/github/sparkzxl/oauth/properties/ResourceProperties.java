package com.github.sparkzxl.oauth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.github.sparkzxl.oauth.properties.ResourceProperties.RESOURCE_PREFIX;

/**
 * description:  oauth resource属性
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = RESOURCE_PREFIX)
public class ResourceProperties {

    public static final String RESOURCE_PREFIX = "oauth.resource";

    /**
     * 需要放行的资源路径
     */
    private String[] ignore;

}
