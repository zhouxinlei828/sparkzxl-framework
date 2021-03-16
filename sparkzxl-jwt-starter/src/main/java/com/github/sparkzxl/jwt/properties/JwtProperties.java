package com.github.sparkzxl.jwt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * description: JWT属性类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = "sparkzxl.jwt")
public class JwtProperties implements Serializable {

    private static final long serialVersionUID = 6685832204171292577L;
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
