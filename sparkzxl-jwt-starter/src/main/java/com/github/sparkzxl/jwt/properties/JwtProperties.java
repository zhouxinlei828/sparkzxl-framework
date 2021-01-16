package com.github.sparkzxl.jwt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * description: JWT属性类
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 07:56:59
 */
@Data
@ConfigurationProperties(prefix = "sparkzxl.jwt")
public class JwtProperties {

    /**
     * 过期时间 2h
     */
    private long expire = 2;

    private TimeUnit unit = TimeUnit.HOURS;
    /**
     * 刷新token的过期时间 8h
     */
    private long refreshExpire = 2;

    private String secret = "123456";

}
