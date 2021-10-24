package com.github.sparkzxl.entity.security;

import com.github.sparkzxl.entity.core.AuthUserInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * description: token实体类
 *
 * @author zhouxinlei
 */
@Data
public class UserToken implements Serializable {

    private static final long serialVersionUID = 7155188090339340337L;
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
    private AuthUserInfo authUserInfo;

    /**
     * 访问令牌头前缀
     */
    private String tokenType;

}
