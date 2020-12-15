package com.github.sparkzxl.user.service.impl;

import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.support.SparkZxlExceptionAssert;
import com.github.sparkzxl.core.utils.BuildKeyUtils;
import com.github.sparkzxl.core.entity.AuthUserInfo;
import com.github.sparkzxl.cache.template.CacheTemplate;
import com.github.sparkzxl.core.support.ResponseResultStatus;
import com.github.sparkzxl.user.service.IAuthUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * description: 全局用户获取 服务类
 *
 * @author: zhouxinlei
 * @date: 2020-07-13 14:27:12
 */
@Slf4j
public class AuthUserInfoServiceServiceImpl implements IAuthUserInfoService {

    @Autowired(required = false)
    public CacheTemplate cacheTemplate;

    @Override
    public AuthUserInfo getUserInfo(String accessToken) {
        log.info("accessToken is {}", accessToken);
        AuthUserInfo authUser = getCache(BuildKeyUtils.generateKey(BaseContextConstants.AUTH_USER, accessToken));
        if (ObjectUtils.isEmpty(authUser)){
            SparkZxlExceptionAssert.businessFail("未获取登录用户信息");
        }
        return authUser;
    }

    @Override
    public AuthUserInfo getCache(String key) {
        if (ObjectUtils.isNotEmpty(cacheTemplate)) {
            return cacheTemplate.get(key);
        }
        SparkZxlExceptionAssert.businessFail("无法获取到缓存，请确认是否开启缓存");
        return null;
    }
}
