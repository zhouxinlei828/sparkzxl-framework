package com.github.sparkzxl.jwt.properties;

import lombok.Data;

/**
 * description: JWT属性类
 *
 * @author zhouxinlei
 */
@Data
public class KeyStoreProperties {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 密钥文件路径
     */
    private String path;
    /**
     * 别名
     */
    private String alias;

    /**
     * 密钥密码
     */
    private String password;

}
