package com.github.sparkzxl.core.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * description：Network工具类
 *
 * @author zhouxinlei
 */
public class NetworkUtil {

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request http请求
     * @return String
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = getHeaderOrNull(request, "X-Forwarded-For");
        ip = validateAndGetIp(ip, request);
        return ip;
    }

    /**
     * 获取header参数
     *
     * @param request    请求
     * @param headerName 请求header参数
     * @return String
     */
    private static String getHeaderOrNull(HttpServletRequest request, String headerName) {
        if (Objects.isNull(request)) {
            return null;
        }
        return request.getHeader(headerName);
    }

    private static String validateAndGetIp(String ip, HttpServletRequest request) {
        if (isInvalidIp(ip)) {
            ip = getHeaderOrNull(request, "Proxy-Client-IP");
            if (isInvalidIp(ip)) {
                ip = getHeaderOrNull(request, "WL-Proxy-Client-IP");
            }
            if (isInvalidIp(ip)) {
                ip = getHeaderOrNull(request, "HTTP_CLIENT_IP");
            }
            if (isInvalidIp(ip)) {
                ip = getHeaderOrNull(request, "HTTP_X_FORWARDED_FOR");
            }
            if (isInvalidIp(ip)) {
                ip = getHeaderOrNull(request, "X-Real-IP");
            }
            if (isInvalidIp(ip)) {
                ip = request.getRemoteAddr();
            }
        }
        if (ip != null && ip.length() > 15) {
            // 使用 Stream API 筛选出第一个非 UNKNOWN 的 IP
            ip = java.util.Arrays.stream(ip.split(","))
                    .filter(s -> !StrPool.UNKNOWN.equalsIgnoreCase(s.trim()))
                    .findFirst()
                    .orElse(ip);
        }
        return ip;
    }

    /**
     * 校验是否有效ip地址
     *
     * @param ip ip地址
     * @return boolean
     */
    private static boolean isInvalidIp(String ip) {
        return Objects.isNull(ip) || ip.isEmpty() || StrPool.UNKNOWN.equalsIgnoreCase(ip);
    }

}
