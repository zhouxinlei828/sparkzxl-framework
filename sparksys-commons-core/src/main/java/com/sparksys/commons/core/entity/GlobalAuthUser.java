package com.sparksys.commons.core.entity;

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
public class GlobalAuthUser implements Serializable {

    private static final long serialVersionUID = -6592610263703423919L;

    private Long id;

    private String account;

    private String password;

    private String name;

    private Boolean status;

    private List<String> roles;

    private List<String> permissions;

}
