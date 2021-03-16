package com.github.sparkzxl.user.service.impl;

import com.github.sparkzxl.cache.template.CacheTemplate;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.context.BaseContextHandler;
import com.github.sparkzxl.core.entity.AuthUserInfo;
import com.github.sparkzxl.core.support.ResponseResultStatus;
import com.github.sparkzxl.core.support.SparkZxlExceptionAssert;
import com.github.sparkzxl.core.utils.BuildKeyUtils;
import com.github.sparkzxl.user.service.IAuthUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * description: 全局用户获取 服务类
 *
 * @author zhouxinlei
 */
@Slf4j
public class AuthUserInfoServiceServiceImpl implements IAuthUserInfoService {

    @Autowired(required = false)
    public RedisTemplate<String, Object> redisTemplate;
    @Autowired(required = false)
    private CacheTemplate cacheTemplate;

    @Override
    public AuthUserInfo getUserInfo(String accessToken) {
        log.info("accessToken is {}", accessToken);
        AuthUserInfo authUserInfo = null;
        if (ObjectUtils.isNotEmpty(cacheTemplate)) {
            authUserInfo = cacheTemplate.get(BuildKeyUtils.generateKey(BaseContextConstants.AUTH_USER, accessToken));
        }
        if (ObjectUtils.isEmpty(authUserInfo)) {
            String userId = BaseContextHandler.getUserId(String.class);
            authUserInfo = getCache(BuildKeyUtils.generateKey(BaseContextConstants.AUTH_USER_TOKEN, userId), accessToken);
            if (ObjectUtils.isEmpty(authUserInfo)) {
                SparkZxlExceptionAssert.businessFail(ResponseResultStatus.JWT_EXPIRED_ERROR);
            }
        }
        return authUserInfo;
    }

    @Override
    public AuthUserInfo getCache(String key, String accessToken) {
        if (ObjectUtils.isNotEmpty(redisTemplate)) {
            return (AuthUserInfo) redisTemplate.opsForHash().get(key, accessToken);
        }
        SparkZxlExceptionAssert.businessFail("无法获取到缓存，请确认是否开启缓存");
        return null;
    }
}
