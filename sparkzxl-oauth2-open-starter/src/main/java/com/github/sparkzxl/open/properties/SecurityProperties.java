package com.github.sparkzxl.open.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description: security 自动装配属性配置
 *
 * @author zhouxinlei
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
    /**
     * 自定义退出登录
     */
    private boolean customLogout = true;
    /**
     * 退出登录响应rest
     */
    private boolean logoutRest = true;
    /**
     * 退出登录后跳转地址
     */
    private String logoutSuccessUrl;
}
