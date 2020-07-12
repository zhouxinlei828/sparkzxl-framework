package com.sparksys.commons.web.component;

import com.sparksys.commons.core.entity.GlobalAuthUser;
import com.sparksys.commons.core.service.AbstractAuthService;
import com.sparksys.commons.web.utils.HttpResponseUtils;
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
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final AbstractAuthService abstractAuthUserRequest;

    public AuthUserArgumentResolver(AbstractAuthService abstractAuthUserRequest) {
        this.abstractAuthUserRequest = abstractAuthUserRequest;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == GlobalAuthUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest
            , WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        assert servletRequest != null;
        String accessToken = HttpResponseUtils.getAuthHeader(servletRequest);
        return abstractAuthUserRequest.getUserInfo(accessToken);
    }
}
