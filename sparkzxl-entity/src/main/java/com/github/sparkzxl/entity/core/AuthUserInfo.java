package com.github.sparkzxl.entity.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * description: 登录认证用户实体类
 *
 * @author zhouxinlei
 */
@Data
public class AuthUserInfo<E> implements Serializable {

    private static final long serialVersionUID = 7289539823055340191L;

    /**
     * 用户id
     */
    private String id;

    /**
     * 账户
     */
    private String account;

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

    /**
     * 扩展数据
     */
    private E detailInfo;

    /**
     * 租户标识
     */
    private String tenantId;

}
