package com.github.sparkzxl.web.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.annotation.result.ResponseResult;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.context.BaseContextHolder;
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
        BaseContextHolder.setTenant(RequestContextHolderUtils.getHeader(request, BaseContextConstants.TENANT_ID));
        BaseContextHolder.setUserId(RequestContextHolderUtils.getHeader(request, BaseContextConstants.JWT_KEY_USER_ID));
        BaseContextHolder.setAccount(RequestContextHolderUtils.getHeader(request, BaseContextConstants.JWT_KEY_ACCOUNT));
        BaseContextHolder.setName(RequestContextHolderUtils.getHeader(request, BaseContextConstants.JWT_KEY_NAME));
        BaseContextHolder.setVersion(RequestContextHolderUtils.getHeader(request, BaseContextConstants.VERSION));
        String traceId = request.getHeader(BaseContextConstants.TRACE_ID_HEADER);
        MDC.put(BaseContextConstants.LOG_TRACE_ID, StrUtil.isEmpty(traceId) ? IdUtil.fastSimpleUUID() : traceId);
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
        BaseContextHolder.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
