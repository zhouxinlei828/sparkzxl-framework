package com.sparksys.commons.user.service.impl;

import com.sparksys.commons.core.constant.CacheKey;
import com.sparksys.commons.core.entity.GlobalAuthUser;
import com.sparksys.commons.core.repository.CacheRepository;
import com.sparksys.commons.core.support.ResponseResultStatus;
import com.sparksys.commons.user.service.IGlobalUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.function.Function;


/**
 * description: 全局用户获取 服务类
 *
 * @author: zhouxinlei
 * @date: 2020-07-13 14:27:12
 */
@Slf4j
@Service
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
