package com.sparksys.commons.jwt.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: JWT属性类
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 07:56:59
 */
@Data
@ConfigurationProperties(prefix = "sparksys.jwt")
public class JwtProperties {

    /**
     * 过期时间 2h
     */
    private Long expire = 7200L;
    /**
     * 刷新token的过期时间 8h
     */
    private Long refreshExpire = 28800L;

}
