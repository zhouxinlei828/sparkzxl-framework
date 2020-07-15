package com.sparksys.commons.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description: security 自动装配属性配置
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 16:24:55
 */
@Data
@ConfigurationProperties(prefix = "sparksys.security")
public class SecurityProperties {

    private boolean enableJwtFilter;

    private boolean dynamicSecurity;

    private List<String> ignoreUrls;

}
