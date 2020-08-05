package com.sparksys.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sparksys.core.constant.BaseContextConstants;
import com.sparksys.core.support.SparkSysExceptionAssert;
import com.sparksys.core.utils.KeyUtils;
import com.sparksys.core.entity.AuthUserInfo;
import com.sparksys.cache.template.CacheTemplate;
import com.sparksys.core.support.ResponseResultStatus;
import com.sparksys.user.service.IAuthUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
        AuthUserInfo authUser = getCache(KeyUtils.buildKey(BaseContextConstants.AUTH_USER, accessToken));
        ResponseResultStatus.UN_AUTHORIZED.assertNotNull(authUser);
        return authUser;
    }

    @Override
    public AuthUserInfo getCache(String key) {
        if (ObjectUtils.isNotEmpty(cacheTemplate)) {
            return cacheTemplate.get(key);
        }
        SparkSysExceptionAssert.businessFail("无法获取到缓存，请确认是否开启缓存支持");
        return null;
    }
}
