package com.sparksys.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * description: HttpServlet工具类
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:45:00
 */
@Slf4j
public class HttpCommonUtils {

    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }


    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        RequestContextHolder.setRequestAttributes(servletRequestAttributes, true);
        return servletRequestAttributes;
    }


    public static void setAttribute(String name, Object o) {
        getRequestAttributes().getRequest().setAttribute(name, o);
    }

    public static String getAttributeStr(String name) {
        return String.valueOf(getRequestAttributes().getRequest().getAttribute(name));
    }

    public static Object getAttribute(String name) {
        return getRequestAttributes().getRequest().getAttribute(name);
    }

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址。
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串
     *
     * @return String
     */
    public static String getIpAddress() {
        HttpServletRequest servletRequest = getRequest();
        String ignoreCase = "unknown";
        String ip = servletRequest.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || ignoreCase.equalsIgnoreCase(ip)) {
            ip = servletRequest.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ignoreCase.equalsIgnoreCase(ip)) {
            ip = servletRequest.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ignoreCase.equalsIgnoreCase(ip)) {
            ip = servletRequest.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || ignoreCase.equalsIgnoreCase(ip)) {
            ip = servletRequest.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || ignoreCase.equalsIgnoreCase(ip)) {
            ip = servletRequest.getRemoteAddr();
            String localIp = "127.0.0.1";
            String ignoreIp = "0:0:0:0:0:0:0:1";
            if (localIp.equals(ip) || ignoreIp.equals(ip)) {
                //根据网卡取本机配置的IP
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.error(e.getMessage());
                }
                assert inetAddress != null;
                ip = inetAddress.getHostAddress();
            }
        }
        return ip;
    }
}
