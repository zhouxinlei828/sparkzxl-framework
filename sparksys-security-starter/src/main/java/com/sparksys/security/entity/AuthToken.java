package com.sparksys.security.entity;

import com.sparksys.core.entity.AuthUserInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * description: token实体类
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:38:01
 */
@Data
public class AuthToken implements Serializable {

    private static final long serialVersionUID = 6385548035127824037L;

    /**
     * token
     */
    private String token;

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
    private String tokenHead;


}
