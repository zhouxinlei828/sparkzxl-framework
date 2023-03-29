package com.github.sparkzxl.jwt.properties;

import static com.github.sparkzxl.jwt.properties.JwtProperties.JWT_PREFIX;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * description: JWT属性类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = JWT_PREFIX)
public class JwtProperties implements Serializable {

    private static final long serialVersionUID = 8890136349889379042L;

    public static final String JWT_PREFIX = "spring.jwt";

    /**
     * 过期时间 2h
     */
    private long expire = 2;

    private TimeUnit unit = TimeUnit.HOURS;
    /**
     * 刷新token的过期时间 8h
     */
    private long refreshExpire = 8;

    private String secret = "123456";

    @NestedConfigurationProperty
    private KeyStoreProperties keyStore = new KeyStoreProperties();

}
