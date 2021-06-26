package com.github.sparkzxl.user.service.impl;

import com.github.sparkzxl.cache.template.GeneralCacheService;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.context.BaseContextHolder;
import com.github.sparkzxl.entity.core.AuthUserInfo;
import com.github.sparkzxl.core.support.BizExceptionAssert;
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

    public RedisTemplate<String, Object> redisTemplate;
    private GeneralCacheService generalCacheService;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setGeneralCacheService(GeneralCacheService generalCacheService) {
        this.generalCacheService = generalCacheService;
    }

    @Override
    public AuthUserInfo getUserInfo(String accessToken) {
        log.info("accessToken : [{}]", accessToken);
        AuthUserInfo authUserInfo = null;
        if (ObjectUtils.isNotEmpty(generalCacheService)) {
            authUserInfo = generalCacheService.get(BuildKeyUtils.generateKey(BaseContextConstants.AUTH_USER, accessToken));
        }
        if (ObjectUtils.isEmpty(authUserInfo)) {
            String userId = BaseContextHolder.getUserId(String.class);
            authUserInfo = getCache(BuildKeyUtils.generateKey(BaseContextConstants.AUTH_USER_TOKEN, userId), accessToken);
            if (ObjectUtils.isEmpty(authUserInfo)) {
                BizExceptionAssert.businessFail(ApiResponseStatus.JWT_EXPIRED_ERROR);
            }
        }
        return authUserInfo;
    }

    @Override
    public AuthUserInfo getCache(String key, String accessToken) {
        if (ObjectUtils.isNotEmpty(redisTemplate)) {
            return (AuthUserInfo) redisTemplate.opsForHash().get(key, accessToken);
        }
        BizExceptionAssert.businessFail("无法获取到缓存，请确认是否开启缓存");
        return null;
    }
}
