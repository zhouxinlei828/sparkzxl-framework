package com.sparksys.oauth.resource.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description:  oauth resource属性
 *
 * @author: zhouxinlei
 * @date: 2020-08-01 13:24:15
 */
@Data
@ConfigurationProperties(prefix = "sparksys.oauth2.resource")
public class ResourceProperties {

    /**
     * 需要放行的资源路径
     */
    private String[] ignorePatterns;

    /**
     * 需要保护的资源路径
     */
    private String[] protectPatterns;
}
