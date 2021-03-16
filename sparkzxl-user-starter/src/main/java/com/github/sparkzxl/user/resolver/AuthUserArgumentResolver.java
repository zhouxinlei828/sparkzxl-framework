package com.github.sparkzxl.user.resolver;

import com.github.sparkzxl.core.base.ResponseResultUtils;
import com.github.sparkzxl.core.entity.AuthUserInfo;
import com.github.sparkzxl.user.service.IAuthUserInfoService;
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
 */
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final IAuthUserInfoService globalUserService;

    public AuthUserArgumentResolver(IAuthUserInfoService globalUserService) {
        this.globalUserService = globalUserService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == AuthUserInfo.class;
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
