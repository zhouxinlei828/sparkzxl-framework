package com.github.sparkzxl.web.context;

import com.github.sparkzxl.core.context.IContextHolder;
import com.github.sparkzxl.core.util.RequestContextHolderUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * description: 服务上下文
 *
 * @author zhouxinlei
 * @since 2022-07-25 14:25:45
 */
public class ServiceContextHolder implements IContextHolder {

    public HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = RequestContextHolderUtils.getRequestAttributes();
        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        return request;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return null;
        }
        return request.getHeaderNames();
    }

    @Override
    public String getHeader(String name) {
        String header = null;
        HttpServletRequest request = getHttpServletRequest();
        if (request != null) {
            header = request.getHeader(name);
        }
        return header;
    }

    @Override
    public String getParameter(String name) {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return null;
        }
        return request.getParameter(name);
    }

    public Cookie getHttpCookie(String name) {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return null;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            String cookieName = cookie.getName();
            if (StringUtils.equals(cookieName, name)) {
                return cookie;
            }
        }
        return null;
    }

    @Override
    public String getCookie(String name) {
        Cookie cookie = getHttpCookie(name);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    @Override
    public String getRouteAddress() {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader("route-address");
    }
}
