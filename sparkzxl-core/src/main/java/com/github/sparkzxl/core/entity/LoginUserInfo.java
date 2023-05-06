package com.github.sparkzxl.core.entity;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * description: 认证用户实体类
 *
 * @author zhouxinlei
 */
@Data
public class LoginUserInfo<E> implements Serializable {

    private static final long serialVersionUID = 7146025746306184119L;

    /**
     * 用户id
     */
    private String id;

    /**
     * 账户
     */
    private String username;

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
    private E customData;

    /**
     * 租户标识
     */
    private String tenantId;

}
