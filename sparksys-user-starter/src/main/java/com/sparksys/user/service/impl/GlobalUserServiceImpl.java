package com.sparksys.user.service.impl;

import com.sparksys.core.constant.CacheKey;
import com.sparksys.core.entity.GlobalAuthUser;
import com.sparksys.core.repository.CacheRepository;
import com.sparksys.core.support.ResponseResultStatus;
import com.sparksys.user.service.IGlobalUserService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;


/**
 * description: 全局用户获取 服务类
 *
 * @author: zhouxinlei
 * @date: 2020-07-13 14:27:12
 */
@Slf4j
public class GlobalUserServiceImpl implements IGlobalUserService {

    @Resource
    public CacheRepository cacheRepository;

    @Override
    public GlobalAuthUser getUserInfo(String accessToken) {
        log.info("accessToken is {}", accessToken);
        GlobalAuthUser authUser = getCache(CacheKey.buildKey(CacheKey.AUTH_USER, accessToken));
        ResponseResultStatus.UN_AUTHORIZED.assertNotNull(authUser);
        return authUser;
    }

    @Override
    public GlobalAuthUser getCache(String key) {
        return cacheRepository.get(key);
    }
}
