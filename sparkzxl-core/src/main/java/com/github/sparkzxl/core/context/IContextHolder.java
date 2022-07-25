package com.github.sparkzxl.core.context;

import java.util.Enumeration;

/**
 * description: ContextHolder
 *
 * @author zhouxinlei
 * @since 2022-07-25 13:58:55
 */
public interface IContextHolder {

    Enumeration<String> getHeaderNames();

    String getHeader(String name);

    String getParameter(String name);

    String getCookie(String name);

    String getRouteAddress();
}
