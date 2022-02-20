package com.github.sparkzxl.security.component;

import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.core.util.HttpRequestUtils;
import com.github.sparkzxl.entity.response.IErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description: 当未登录或者token失效访问接口时，自定义的返回结果
 *
 * @author zhouxinlei
 */
@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) {
        log.error("AuthenticationException：[{}]", e.getMessage());
        IErrorCode errorCode = ExceptionErrorCode.UN_AUTHORIZED;
        if (e instanceof AccountExpiredException) {
            errorCode = ExceptionErrorCode.TOKEN_EXPIRED_ERROR;
        }
        HttpRequestUtils.failResponse(response,errorCode);
    }

}
