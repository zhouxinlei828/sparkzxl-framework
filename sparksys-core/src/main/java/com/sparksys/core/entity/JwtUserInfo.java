package com.sparksys.core.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * description:JWT中存储的信息
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 07:51:20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class JwtUserInfo {
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
    private Long expire;
    /**
     * JWT的ID
     */
    private String jti;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户拥有的权限
     */
    private List<String> authorities;
}
