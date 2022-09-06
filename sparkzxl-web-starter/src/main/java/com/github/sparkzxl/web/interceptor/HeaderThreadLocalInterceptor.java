package com.github.sparkzxl.web.interceptor;

import cn.hutool.core.convert.Convert;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.util.RequestContextHolderUtils;
import com.github.sparkzxl.web.annotation.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * description: 全局请求拦截处理
 *
 * @author zhouxinlei
 */
@Slf4j
public class HeaderThreadLocalInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        //设置当前请求线程全局信息
        RequestLocalContextHolder.setTenant(request.getHeader(BaseContextConstants.TENANT_ID));
        RequestLocalContextHolder.setUserId(request.getHeader(BaseContextConstants.JWT_KEY_USER_ID));
        RequestLocalContextHolder.setAccount(request.getHeader(BaseContextConstants.JWT_KEY_ACCOUNT));
        RequestLocalContextHolder.setName(request.getHeader(BaseContextConstants.JWT_KEY_NAME));
        RequestLocalContextHolder.setVersion(request.getHeader(BaseContextConstants.VERSION));
        MDC.put(BaseContextConstants.TENANT_ID, RequestContextHolderUtils.getHeader(request, BaseContextConstants.TENANT_ID));
        MDC.put(BaseContextConstants.JWT_KEY_USER_ID, RequestContextHolderUtils.getHeader(request, BaseContextConstants.JWT_KEY_USER_ID));
        Boolean feign = Convert.toBool(request.getHeader(BaseContextConstants.REMOTE_CALL), Boolean.FALSE);
        if (feign) {
            return true;
        }

        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            final Class<?> classz = handlerMethod.getBeanType();
            final Method method = handlerMethod.getMethod();
            if (classz.isAnnotationPresent(Response.class)) {
                request.setAttribute(BaseContextConstants.RESPONSE_RESULT_ANN, classz.getAnnotation(Response.class));
            } else if (method.isAnnotationPresent(Response.class)) {
                request.setAttribute(BaseContextConstants.RESPONSE_RESULT_ANN, method.getAnnotation(Response.class));
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestLocalContextHolder.remove();
    }
}
