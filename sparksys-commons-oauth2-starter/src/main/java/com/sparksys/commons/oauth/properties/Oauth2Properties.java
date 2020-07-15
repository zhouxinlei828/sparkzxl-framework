package com.sparksys.commons.oauth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description: oauth2 自动装配属性配置
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 16:24:55
 */
@Data
@ConfigurationProperties(prefix = "sparksys.oauth2")
public class Oauth2Properties {

    /**
     * client_id
     */
    private String clientId;

    /**
     * client_secret
     */
    private String clientSecret;

    /**
     * 访问token的有效期
     */
    private int accessTokenValiditySeconds = 3600;

    /**
     * 刷新token的有效期
     */
    private int refreshTokenValiditySeconds = 864000;

    /**
     * redirect_uri，用于授权成功后跳转
     */
    private String registeredRedirectUris;

    /**
     * 申请的权限范围
     */
    private String[] scopes;

    /**
     * 授权类型:authorization_code,password,refresh_token
     */
    private String[] authorizedGrantTypes;

    /**
     * 需要保护的资源路径
     */
    private String[] protectPatterns;
}
