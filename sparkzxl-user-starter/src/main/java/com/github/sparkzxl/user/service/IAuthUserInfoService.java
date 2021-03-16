package com.github.sparkzxl.user.service;

import com.github.sparkzxl.core.entity.AuthUserInfo;

/**
 * description: 全局用户获取 服务类
 *
 * @author zhouxinlei
 */
public interface IAuthUserInfoService {

    /**
     * 根据token获取认证用户
     *
     * @param accessToken token
     * @return GlobalAuthUser
     */
    AuthUserInfo getUserInfo(String accessToken);

    /**
     * 缓存中获取用户信息
     *
     * @param key         缓存key
     * @param accessToken token∂
     * @return GlobalAuthUser
     */
    AuthUserInfo getCache(String key, String accessToken);
}
