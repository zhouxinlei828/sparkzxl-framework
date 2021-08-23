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


    public static void setAttribute(String name, Object val) {
        getRequestAttributes().getRequest().setAttribute(name, val);
    }

    public static void removeAttribute(String key) {
        getRequestAttributes().getRequest().removeAttribute(key);
    }

    public static String getAttributeStr(String key) {
        return String.valueOf(getRequestAttributes().getRequest().getAttribute(key));
    }

    public static Object getAttribute(String key) {
        return getRequestAttributes().getRequest().getAttribute(key);
    }

    public static String getHeader(HttpServletRequest request, String key) {
        String value = request.getHeader(key);
        if (StrUtil.isEmpty(value)) {
            return StrPool.EMPTY;
        }
        return URLUtil.decode(value);
    }

    public static String getHeader(String key) {
        String value = getRequest().getHeader(key);
        if (StrUtil.isEmpty(value)) {
            return StrPool.EMPTY;
        }
        return URLUtil.decode(value);
    }

}
