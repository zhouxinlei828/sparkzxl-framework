package com.sparksys.core.entity;

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

    private Long id;

    private String account;

    private String password;

    private String name;

    private Boolean status;

    private List<String> roleList;

    private List<String> authorityList;

}
