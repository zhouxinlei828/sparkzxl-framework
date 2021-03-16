package com.github.sparkzxl.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description: security 自动装配属性配置
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = "sparkzxl.security")
public class SecurityProperties {

    private List<String> ignorePatterns;

    private boolean builtInPermissions;

}
