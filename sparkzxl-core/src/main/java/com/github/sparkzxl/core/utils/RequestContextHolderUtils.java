package com.github.sparkzxl.core.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description: HttpServlet工具类
 *
 * @author zhouxinlei
 */
@Slf4j
public class RequestContextHolderUtils {

    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }


    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(servletRequestAttributes, true);
        return servletRequestAttributes;
    }


    public static void setAttribute(String name, Object o) {
        getRequestAttributes().getRequest().setAttribute(name, o);
    }

    public static void removeAttribute(String name) {
        getRequestAttributes().getRequest().removeAttribute(name);
    }

    public static String getAttributeStr(String name) {
        return String.valueOf(getRequestAttributes().getRequest().getAttribute(name));
    }

    public static Object getAttribute(String name) {
        return getRequestAttributes().getRequest().getAttribute(name);
    }

    public static String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (StrUtil.isEmpty(value)) {
            return StrPool.EMPTY;
        }
        return URLUtil.decode(value);
    }

    public static String getHeader(String name) {
        String value = getRequest().getHeader(name);
        if (StrUtil.isEmpty(value)) {
            return StrPool.EMPTY;
        }
        return URLUtil.decode(value);
    }

}
