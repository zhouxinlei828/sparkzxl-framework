package com.github.sparkzxl.user.manager;

import com.github.sparkzxl.cache.service.CacheService;
import com.github.sparkzxl.core.constant.BaseContextConstants;
import com.github.sparkzxl.core.support.TokenExpireException;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import com.github.sparkzxl.core.util.ArgumentAssert;
import com.github.sparkzxl.core.util.HttpRequestUtils;
import com.github.sparkzxl.core.util.KeyGeneratorUtil;
import java.time.Duration;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

/**
 * description: 用户状态管理实现
 *
 * @author zhouxinlei
 */
@Slf4j
public class DefaultUserStateManager implements UserStateManager {

    @Resource
    private CacheService cacheService;

    @Override
    public void addUser(String token, Object userinfo, Duration timeOut) {
        if (ObjectUtils.isNotEmpty(cacheService)) {
            cacheService.set(KeyGeneratorUtil.generateKey(BaseContextConstants.AUTH_USER_TOKEN, token), userinfo, timeOut);
        }
    }

    @Override
    public void removeUser(String token) {
        if (ObjectUtils.isNotEmpty(cacheService)) {
            cacheService.remove(KeyGeneratorUtil.generateKey(BaseContextConstants.AUTH_USER_TOKEN, token));
        }
    }

    @Override
    public Object getUser(String token) {
        log.info("user token : [{}]", token);
        Object userinfo = null;
        if (ObjectUtils.isNotEmpty(cacheService)) {
            userinfo = cacheService.get(KeyGeneratorUtil.generateKey(BaseContextConstants.AUTH_USER_TOKEN, token));
        }
        ArgumentAssert.notNull(userinfo, () -> new TokenExpireException(ResultErrorCode.LOGIN_EXPIRE));
        return userinfo;
    }

    @Override
    public Object getUser(HttpServletRequest servletRequest) {
        String token = HttpRequestUtils.getAuthHeader(servletRequest);
        return getUser(token);
    }

    @Override
    public Object getUserCache(String key) {
        return cacheService.get(key);
    }
}
