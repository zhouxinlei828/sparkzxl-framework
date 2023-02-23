package com.github.sparkzxl.web.interceptor;

import cn.hutool.core.convert.Convert;
import com.github.sparkzxl.core.constant.BaseContextConstants;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.util.RequestContextUtils;
import com.github.sparkzxl.spi.Join;
import com.github.sparkzxl.web.annotation.Response;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * description: 请求上下文拦截器
 *
 * @author zhouxinlei
 * @since 2022-12-09 09:07:20
 */
@Join
public class RequestContextInnerInterceptor extends AbstractInnerInterceptor {

    @Override
    public void doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return;
        }

        //设置当前请求线程全局信息
        RequestLocalContextHolder.setTenant(request.getHeader(BaseContextConstants.TENANT_ID));
        RequestLocalContextHolder.setUserId(request.getHeader(BaseContextConstants.JWT_KEY_USER_ID));
        RequestLocalContextHolder.setAccount(request.getHeader(BaseContextConstants.JWT_KEY_ACCOUNT));
        RequestLocalContextHolder.setName(request.getHeader(BaseContextConstants.JWT_KEY_NAME));
        RequestLocalContextHolder.setVersion(request.getHeader(BaseContextConstants.VERSION));
        String traceId = request.getHeader(BaseContextConstants.TRACE_ID_HEADER);
        MDC.put(BaseContextConstants.LOG_TRACE_ID, traceId);
        MDC.put(BaseContextConstants.TENANT_ID, RequestContextUtils.getHeader(request, BaseContextConstants.TENANT_ID));
        MDC.put(BaseContextConstants.JWT_KEY_USER_ID, RequestContextUtils.getHeader(request, BaseContextConstants.JWT_KEY_USER_ID));
        Boolean feign = Convert.toBool(request.getHeader(BaseContextConstants.REMOTE_CALL), Boolean.FALSE);
        if (feign) {
            return;
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
    }

    @Override
    public void doPostHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public int getOrder() {
        return -99;
    }
}
