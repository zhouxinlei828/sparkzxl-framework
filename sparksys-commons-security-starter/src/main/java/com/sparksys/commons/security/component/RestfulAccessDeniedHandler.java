package com.sparksys.commons.security.component;

import com.sparksys.commons.web.utils.HttpResponseUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * description: 当访问接口没有权限时，自定义的返回结果
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:35:14
 */
public class RestfulAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException e) throws IOException, ServletException {
        HttpResponseUtils.forbidden(response);
    }
}
