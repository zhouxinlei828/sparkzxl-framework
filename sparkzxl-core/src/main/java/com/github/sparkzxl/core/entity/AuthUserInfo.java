package com.github.sparkzxl.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * description: 登录认证用户实体类
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:47:33
 */
@Data
public class AuthUserInfo implements Serializable {

    private static final long serialVersionUID = 5082256738321189608L;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 账户
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 账户状态
     */
    private Boolean status;

    /**
     * 角色列表
     */
    private List<String> roleList;

    /**
     * 权限列表
     */
    private List<String> authorityList;

}
