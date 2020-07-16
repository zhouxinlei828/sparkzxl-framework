package com.sparksys.oauth.rest;

import com.sparksys.core.base.api.ResponseResultUtils;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description: 当访问接口没有权限时，自定义的返回结果
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:35:14
 */
public class OauthRestfulAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException exception) {
        ResponseResultUtils.forbidden(response);
    }
}
