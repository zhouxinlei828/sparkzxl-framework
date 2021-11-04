package com.github.sparkzxl.user.manager;

import com.github.sparkzxl.cache.service.GeneralCacheService;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.context.ResponseHelper;
import com.github.sparkzxl.core.support.ExceptionAssert;
import com.github.sparkzxl.core.utils.BuildKeyUtil;
import com.github.sparkzxl.entity.core.AuthUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * description: 用户状态管理实现
 *
 * @author zhouxinlei
 * @date 2021-11-04 15:13:30
 */
@Slf4j
public class DefaultUserStateManager implements UserStateManager {

    private GeneralCacheService generalCacheService;

    @Autowired
    public void setGeneralCacheService(GeneralCacheService generalCacheService) {
        this.generalCacheService = generalCacheService;
    }

    @Override
    public void addUser(String token, AuthUserInfo authUserInfo, int expiresIn, TimeUnit timeUnit) {
        if (ObjectUtils.isNotEmpty(generalCacheService)) {
            generalCacheService.set(BuildKeyUtil.generateKey(BaseContextConstants.AUTH_USER_TOKEN, token), authUserInfo, (long) expiresIn, timeUnit);
        }
    }

    @Override
    public void removeUser(String token) {
        if (ObjectUtils.isNotEmpty(generalCacheService)) {
            generalCacheService.remove(BuildKeyUtil.generateKey(BaseContextConstants.AUTH_USER_TOKEN, token));
        }
    }

    @Override
    public AuthUserInfo getUser(String token) {
        log.info("user token : [{}]", token);
        AuthUserInfo authUserInfo = null;
        if (ObjectUtils.isNotEmpty(generalCacheService)) {
            authUserInfo = generalCacheService.get(BuildKeyUtil.generateKey(BaseContextConstants.AUTH_USER_TOKEN, token));
        }
        if (ObjectUtils.isEmpty(authUserInfo)) {
            ExceptionAssert.failure(ResponseInfoStatus.NO_FOUND_USER);
        }
        return authUserInfo;
    }

    @Override
    public AuthUserInfo getUser(HttpServletRequest servletRequest) {
        String token = ResponseHelper.getAuthHeader(servletRequest);
        return getUser(token);
    }

    @Override
    public AuthUserInfo getUserCache(String key) {
        return generalCacheService.get(key);
    }
}
