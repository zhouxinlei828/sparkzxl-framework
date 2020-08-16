package com.sparksys.web.interceptor;

import com.sparksys.core.base.ResponseResultUtils;
import com.sparksys.core.constant.BaseContextConstant;
import com.sparksys.core.constant.CoreConstant;
import com.sparksys.core.entity.AuthUserInfo;
import com.sparksys.cache.template.CacheTemplate;
import com.sparksys.core.utils.KeyUtils;
import com.sparksys.web.annotation.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

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

    @Autowired(required = false)
    public CacheTemplate cacheTemplate;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (ObjectUtils.isNotEmpty(cacheTemplate)) {
            String accessToken = ResponseResultUtils.getAuthHeader(request);
            AuthUserInfo authUser = cacheTemplate.get(KeyUtils.buildKey(BaseContextConstant.AUTH_USER, accessToken));
            if (ObjectUtils.isNotEmpty(authUser)) {
                request.setAttribute(BaseContextConstant.APPLICATION_AUTH_USER_ID, authUser.getId());
                request.setAttribute(BaseContextConstant.APPLICATION_AUTH_ACCOUNT, authUser.getAccount());
                request.setAttribute(BaseContextConstant.APPLICATION_AUTH_NAME, authUser.getName());
            }
        }
        String feign = request.getHeader(CoreConstant.REQUEST_TYPE);
        if (StringUtils.isNotEmpty(feign)) {
            return true;
        }
        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            final Class<?> classz = handlerMethod.getBeanType();
            final Method method = handlerMethod.getMethod();
            if (classz.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute(CoreConstant.RESPONSE_RESULT_ANN, classz.getAnnotation(ResponseResult.class));
            } else if (method.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute(CoreConstant.RESPONSE_RESULT_ANN, method.getAnnotation(ResponseResult.class));
            }
        }
        return true;
    }
}
