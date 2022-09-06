package com.github.sparkzxl.user.manager;

import com.github.sparkzxl.entity.core.AuthUserInfo;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;

/**
 * description: 用户状态管理
 *
 * @author zhouxinlei
 */
public interface UserStateManager {

    /**
     * 获取请求用户信息
     *
     * @param token        用户token
     * @param authUserInfo 用户
     * @param timeOut      有效期
     */
    void addUser(String token, AuthUserInfo authUserInfo, Duration timeOut);

    /**
     * 移除用户信息
     *
     * @param token 用户token
     */
    void removeUser(String token);

    /**
     * 获取请求用户信息
     *
     * @param token 用户token
     * @return AuthUserInfo
     */
    AuthUserInfo getUser(String token);

    /**
     * 获取用户信息
     *
     * @param servletRequest 用户查询key
     * @return AuthUserInfo
     */
    AuthUserInfo getUser(HttpServletRequest servletRequest);

    /**
     * 获取缓存用户信息
     *
     * @param key 用户查询key
     * @return AuthUserInfo
     */
    AuthUserInfo getUserCache(String key);

}
