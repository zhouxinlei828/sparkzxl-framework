package com.sparksys.commons.jwt.properties;

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

    private String secret;

    private KeyStore keyStore;

    /**
     * description: 公钥和私钥来进行签名和验证
     *
     * @author: zhouxinlei
     * @date: 2020-07-15 19:35:38
    */
    @Data
    public static class KeyStore {

        /**
         * 是否启用
         */
        private boolean enable;

        /**
         * 密钥文件路径
         */
        private String path;

        /**
         * 密钥密码
         */
        private String password;

    }

}
