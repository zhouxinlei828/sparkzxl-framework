package com.github.sparkzxl.gateway.plugin.forwardauth;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * description: forward-auth 插件使用的是经典外部认证。当身份认证失败时，可以实现自定义错误或者重定向到认证页面的场景。
 *
 * @author zhouxinlei
 * @since 2024-06-28 11:13:28
 */
@Data
public class ForwardAuthConfig implements Serializable {

    private static final long serialVersionUID = -862267947514580951L;
    /**
     * 设置 authorization 服务的地址 (例如：http://localhost:9188)。
     */
    private String uri;

    /**
     * 客户端向 authorization 服务发送请求的方法。当设置为 POST 时，会将 request body 转发至 authorization 服务。
     */
    private String requestMethod;

    /**
     * 设置需要由客户端转发到 authorization 服务的请求头。如果没有设置，则只发送提供的 headers (例如：X-Forwarded-XXX)。
     */
    private List<String> requestHeaders;

    /**
     * 设置授权服务出现网络错误时返回给客户端的 HTTP 状态。默认状态为“401”。
     */
    private boolean statusOnError;
}
