package com.github.sparkzxl.core.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * description:JWT中存储的信息
 *
 * @author zhouxinlei
 */
@Data
public class JwtUserInfo<T> {

    private T id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * clientId
     */
    private String clientId;
    /**
     * 主题
     */
    private String sub;
    /**
     * 签发时间
     */
    private Long iat;
    /**
     * 过期时间
     */
    private Date expire;
    /**
     * JWT的ID
     */
    private String jti;
    /**
     * 用户拥有的权限
     */
    private List<String> authorities;

    /**
     * 租户标识
     */
    private String realm;

    /**
     * 是否领域用户
     */
    private boolean realmStatus;
}
