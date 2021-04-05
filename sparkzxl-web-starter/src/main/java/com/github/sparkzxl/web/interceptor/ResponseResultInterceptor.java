package com.github.sparkzxl.web.interceptor;

import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.core.annotation.ResponseResult;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.context.BaseContextHandler;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * description: 全局请求拦截处理
 *
 * @author zhouxinlei
 */
@Slf4j
public class ResponseResultInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //设置当前请求线程全局信息
        if (!BaseContextHandler.getBoot()) {
            BaseContextHandler.setUserId(RequestContextHolderUtils.getHeader(request, BaseContextConstants.JWT_KEY_USER_ID));
            BaseContextHandler.setAccount(RequestContextHolderUtils.getHeader(request, BaseContextConstants.JWT_KEY_ACCOUNT));
            BaseContextHandler.setName(RequestContextHolderUtils.getHeader(request, BaseContextConstants.JWT_KEY_NAME));
            BaseContextHandler.setRealm(RequestContextHolderUtils.getHeader(request, BaseContextConstants.JWT_KEY_REALM));
            String traceId = request.getHeader(BaseContextConstants.TRACE_ID_HEADER);
            MDC.put(BaseContextConstants.LOG_TRACE_ID, StrUtil.isEmpty(traceId) ? StrUtil.EMPTY : traceId);
            MDC.put(BaseContextConstants.JWT_KEY_REALM, RequestContextHolderUtils.getHeader(request, BaseContextConstants.JWT_KEY_REALM));
            MDC.put(BaseContextConstants.JWT_KEY_USER_ID, RequestContextHolderUtils.getHeader(request, BaseContextConstants.JWT_KEY_USER_ID));
            String feign = request.getHeader(BaseContextConstants.REMOTE_CALL);
            if (StringUtils.isNotEmpty(feign)) {
                return true;
            }
        }

        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            final Class<?> classz = handlerMethod.getBeanType();
            final Method method = handlerMethod.getMethod();
            if (classz.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute(BaseContextConstants.RESPONSE_RESULT_ANN, classz.getAnnotation(ResponseResult.class));
            } else if (method.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute(BaseContextConstants.RESPONSE_RESULT_ANN, method.getAnnotation(ResponseResult.class));
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
