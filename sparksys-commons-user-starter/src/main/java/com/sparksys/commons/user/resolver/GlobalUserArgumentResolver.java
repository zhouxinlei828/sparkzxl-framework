package com.sparksys.commons.user.resolver;

import com.sparksys.commons.core.entity.GlobalAuthUser;
import com.sparksys.commons.core.utils.ResponseResultUtils;
import com.sparksys.commons.user.service.IGlobalUserService;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * description: 全局获取用户信息
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:41:46
 */
public class GlobalUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final IGlobalUserService globalUserService;

    public GlobalUserArgumentResolver(IGlobalUserService globalUserService) {
        this.globalUserService = globalUserService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == GlobalAuthUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest
            , WebDataBinderFactory binderFactory) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        assert servletRequest != null;
        String accessToken = ResponseResultUtils.getAuthHeader(servletRequest);
        return globalUserService.getUserInfo(accessToken);
    }
}
