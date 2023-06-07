package com.github.sparkzxl.user.manager;

import java.time.Duration;
import javax.servlet.http.HttpServletRequest;

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
     * @param userinfo 用户
     * @param timeOut      有效期
     */
    void addUser(String token, Object userinfo, Duration timeOut);

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
     * @return LoginUserInfo
     */
    Object getUser(String token);

    /**
     * 获取用户信息
     *
     * @param servletRequest 用户查询key
     * @return LoginUserInfo
     */
    Object getUser(HttpServletRequest servletRequest);

    /**
     * 获取缓存用户信息
     *
     * @param key 用户查询key
     * @return LoginUserInfo
     */
    Object getUserCache(String key);

}
