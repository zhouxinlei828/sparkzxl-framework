package com.github.sparkzxl.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static com.github.sparkzxl.security.properties.SecurityProperties.SECURITY_PREFIX;

/**
 * description: security 自动装配属性配置
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = SECURITY_PREFIX)
public class SecurityProperties {

    public static final String SECURITY_PREFIX = "security";

    private List<String> ignore;

    private boolean allowUrlCtrl;

}
