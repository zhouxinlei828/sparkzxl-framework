package com.github.sparkzxl.open.properties;

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
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private List<String> ignorePatterns;

    private List<String> ignoreStaticPatterns;

    private boolean restAuthentication;

    private boolean customLogin;

    private boolean csrf = true;

    private String preHandleFilter;

}
