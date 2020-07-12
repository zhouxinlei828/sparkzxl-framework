package com.sparksys.commons.security.component;

import com.sparksys.commons.web.utils.HttpResponseUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description: 当未登录或者token失效访问接口时，自定义的返回结果
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:35:00
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {
        HttpResponseUtils.unauthorized(response);
    }

}
