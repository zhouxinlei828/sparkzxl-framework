package com.github.sparkzxl.entity.security;

import lombok.Data;

import java.io.Serializable;

/**
 * description: token实体类
 *
 * @author zhouxinlei
 */
@Data
public class AuthToken implements Serializable {

    private static final long serialVersionUID = 7794601812288371305L;

    /**
     * token
     */
    private String accessToken;

    /**
     * 有效期
     */
    private Long expiration;

    /**
     * 登录用户信息
     */
    private AuthUserDetail authUserDetail;

    /**
     * 访问令牌头前缀
     */
    private String tokenType;


}
