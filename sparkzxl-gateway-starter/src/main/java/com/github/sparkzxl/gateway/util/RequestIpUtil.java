package com.github.sparkzxl.gateway.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Objects;

/**
 * description: 请求Ip工具
 *
 * @author zhouxinlei
 * @date 2021-11-26 15:52:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestIpUtil {

    /**
     * 获取请求的真实ip
     *
     * @param exchange
     * @return
     */
    public static String getReallyIp(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        List<String> strings = headers.get("x-forwarded-for");
        if (Objects.isNull(strings) || strings.isEmpty()) {
            return request.getRemoteAddress().getAddress().toString();
        } else {
            return strings.get(0).split(",")[0];
        }
    }


    public static String getIp(ServerHttpRequest request) {
        String ip = "";
        HttpHeaders headers = request.getHeaders();
        if (!StringUtils.isEmpty(ip = headers.getFirst("X-Forwarded-For"))) {
            // X-FORWARDED-FOR 的第一个ip为真实ip
            return ip.split(",")[0];
        }

        if (!StringUtils.isEmpty(ip = headers.getFirst("Proxy-Client-IP"))) {
            return ip;
        }

        if (!StringUtils.isEmpty(ip = headers.getFirst("WL-Proxy-Client-IP"))) {
            return ip;
        }

        if (!StringUtils.isEmpty(ip = headers.getFirst("HTTP_CLIENT_IP"))) {
            return ip;
        }

        if (!StringUtils.isEmpty(ip = headers.getFirst("HTTP_X_FORWARDED_FOR"))) {
            return ip;
        }
        return request.getRemoteAddress().getHostString();
    }

}
