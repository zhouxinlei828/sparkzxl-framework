package com.github.sparkzxl.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.sparkzxl.cache.template.GeneralCacheService;
import com.github.sparkzxl.constant.AppContextConstants;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.support.ExceptionAssert;
import com.github.sparkzxl.core.utils.BuildKeyUtil;
import com.github.sparkzxl.entity.core.AuthUserInfo;
import com.github.sparkzxl.entity.core.JwtUserInfo;
import com.github.sparkzxl.user.service.IAuthUserInfoService;
import com.nimbusds.jose.JWSObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;


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
            authUserInfo = generalCacheService.get(BuildKeyUtil.generateKey(AppContextConstants.AUTH_USER, accessToken));
        }
        if (ObjectUtils.isEmpty(authUserInfo)) {
            JWSObject jwsObject = null;
            try {
                jwsObject = JWSObject.parse(accessToken);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String payload = jwsObject.getPayload().toString();
            JwtUserInfo jwtUserInfo = JSONObject.parseObject(payload, JwtUserInfo.class);
            Object userInfoId = jwtUserInfo.getId();
            authUserInfo = getCache(BuildKeyUtil.generateKey(AppContextConstants.AUTH_USER_TOKEN, userInfoId), accessToken);
            if (ObjectUtils.isEmpty(authUserInfo)) {
                ExceptionAssert.failure(ApiResponseStatus.TOKEN_EXPIRED_ERROR);
            }
        }
        return authUserInfo;
    }

    @Override
    public AuthUserInfo getCache(String key, String accessToken) {
        if (ObjectUtils.isNotEmpty(redisTemplate)) {
            return (AuthUserInfo) redisTemplate.opsForHash().get(key, accessToken);
        }
        ExceptionAssert.failure("无法获取到缓存，请确认是否开启缓存");
        return null;
    }
}
