package com.sparksys.commons.user.service;

import com.sparksys.commons.core.entity.GlobalAuthUser;

import java.util.function.Function;

/**
 * description: 全局用户获取 服务类
 *
 * @author: zhouxinlei
 * @date: 2020-07-13 14:25:46
 */
public interface IGlobalUserService {

    /**
     * 根据token获取认证用户
     *
     * @param accessToken token
     * @return GlobalAuthUser
     */
    GlobalAuthUser getUserInfo(String accessToken);

    /**
     * 缓存中获取用户信息
     *
     * @param key      缓存key
     * @return GlobalAuthUser
     */
    GlobalAuthUser getCache(String key);
}
