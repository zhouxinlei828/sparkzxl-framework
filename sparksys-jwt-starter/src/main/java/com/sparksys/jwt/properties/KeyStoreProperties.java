package com.sparksys.jwt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: JWT属性类
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 07:56:59
 */
@Data
@ConfigurationProperties(prefix = "sparksys.key-store")
public class KeyStoreProperties {

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
