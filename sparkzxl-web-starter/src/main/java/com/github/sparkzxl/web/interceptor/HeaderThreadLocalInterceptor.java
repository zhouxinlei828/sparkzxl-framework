package com.github.sparkzxl.web.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.annotation.result.ResponseResult;
import com.github.sparkzxl.constant.AppContextConstants;
import com.github.sparkzxl.core.context.AppContextHolder;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import lombok.extern.slf4j.Slf4j;
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
public class HeaderThreadLocalInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        //设置当前请求线程全局信息
        AppContextHolder.setTenant(RequestContextHolderUtils.getHeader(request, AppContextConstants.TENANT));
        AppContextHolder.setUserId(RequestContextHolderUtils.getHeader(request, AppContextConstants.JWT_KEY_USER_ID));
        AppContextHolder.setAccount(RequestContextHolderUtils.getHeader(request, AppContextConstants.JWT_KEY_ACCOUNT));
        AppContextHolder.setName(RequestContextHolderUtils.getHeader(request, AppContextConstants.JWT_KEY_NAME));
        String traceId = request.getHeader(AppContextConstants.TRACE_ID_HEADER);
        MDC.put(AppContextConstants.LOG_TRACE_ID, StrUtil.isEmpty(traceId) ? IdUtil.fastSimpleUUID() : traceId);
        MDC.put(AppContextConstants.TENANT, RequestContextHolderUtils.getHeader(request, AppContextConstants.TENANT));
        MDC.put(AppContextConstants.JWT_KEY_USER_ID, RequestContextHolderUtils.getHeader(request, AppContextConstants.JWT_KEY_USER_ID));
        Boolean feign = Convert.toBool(request.getHeader(AppContextConstants.REMOTE_CALL), Boolean.FALSE);
        if (feign) {
            return true;
        }

        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            final Class<?> classz = handlerMethod.getBeanType();
            final Method method = handlerMethod.getMethod();
            if (classz.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute(AppContextConstants.RESPONSE_RESULT_ANN, classz.getAnnotation(ResponseResult.class));
            } else if (method.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute(AppContextConstants.RESPONSE_RESULT_ANN, method.getAnnotation(ResponseResult.class));
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AppContextHolder.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
