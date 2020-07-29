package com.sparksys.web.interceptor;

import com.sparksys.core.base.api.ResponseResultUtils;
import com.sparksys.core.constant.BaseContextConstants;
import com.sparksys.core.entity.AuthUserInfo;
import com.sparksys.core.repository.CacheRepository;
import com.sparksys.core.utils.KeyUtils;
import com.sparksys.web.annotation.ResponseResult;
import com.sparksys.web.constant.WebConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * description: 全局请求拦截处理
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:42:17
 */
@Slf4j
public class ResponseResultInterceptor extends HandlerInterceptorAdapter {

    @Resource
    public CacheRepository cacheRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            final Class<?> classz = handlerMethod.getBeanType();
            final Method method = handlerMethod.getMethod();
            if (classz.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute(WebConstant.RESPONSE_RESULT_ANN, classz.getAnnotation(ResponseResult.class));
            } else if (method.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute(WebConstant.RESPONSE_RESULT_ANN, method.getAnnotation(ResponseResult.class));
            }
        }
        String accessToken = ResponseResultUtils.getAuthHeader(request);
        AuthUserInfo authUser = cacheRepository.get(KeyUtils.buildKey(BaseContextConstants.AUTH_USER, accessToken));
        if (ObjectUtils.isNotEmpty(authUser)) {
            cacheRepository.set(BaseContextConstants.APPLICATION_AUTH_USER_ID, authUser.getId());
            cacheRepository.set(BaseContextConstants.APPLICATION_AUTH_ACCOUNT, authUser.getAccount());
            cacheRepository.set(BaseContextConstants.APPLICATION_AUTH_NAME, authUser.getName());
        }
        return true;
    }
}
