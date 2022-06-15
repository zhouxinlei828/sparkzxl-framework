package com.github.sparkzxl.user.manager;

import com.github.sparkzxl.cache.service.CacheService;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.core.support.ExceptionAssert;
import com.github.sparkzxl.core.util.HttpRequestUtils;
import com.github.sparkzxl.core.util.KeyGeneratorUtil;
import com.github.sparkzxl.entity.core.AuthUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * description: 用户状态管理实现
 *
 * @author zhouxinlei
 */
@Slf4j
public class DefaultUserStateManager implements UserStateManager {

    private CacheService cacheService;

    @Autowired
    public void setGeneralCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public void addUser(String token, AuthUserInfo authUserInfo, Duration timeOut) {
        if (ObjectUtils.isNotEmpty(cacheService)) {
            cacheService.set(KeyGeneratorUtil.generateKey(BaseContextConstants.AUTH_USER_TOKEN, token), authUserInfo, timeOut);
        }
    }

    @Override
    public void removeUser(String token) {
        if (ObjectUtils.isNotEmpty(cacheService)) {
            cacheService.remove(KeyGeneratorUtil.generateKey(BaseContextConstants.AUTH_USER_TOKEN, token));
        }
    }

    @Override
    public AuthUserInfo getUser(String token) {
        log.info("user token : [{}]", token);
        AuthUserInfo authUserInfo = null;
        if (ObjectUtils.isNotEmpty(cacheService)) {
            authUserInfo = cacheService.get(KeyGeneratorUtil.generateKey(BaseContextConstants.AUTH_USER_TOKEN, token));
        }
        if (ObjectUtils.isEmpty(authUserInfo)) {
            ExceptionAssert.failure(ExceptionErrorCode.USER_NOT_FOUND);
        }
        return authUserInfo;
    }

    @Override
    public AuthUserInfo getUser(HttpServletRequest servletRequest) {
        String token = HttpRequestUtils.getAuthHeader(servletRequest);
        return getUser(token);
    }

    @Override
    public AuthUserInfo getUserCache(String key) {
        return cacheService.get(key);
    }
}
