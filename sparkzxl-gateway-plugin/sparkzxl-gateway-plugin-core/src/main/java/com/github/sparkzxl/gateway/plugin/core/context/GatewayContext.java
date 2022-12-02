package com.github.sparkzxl.gateway.plugin.core.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * description: Context Use gateway Request Content
 *
 * @author zhouxinlei
 * @since 2022-01-10 10:05:12
 */
@Getter
@Setter
@ToString
public class GatewayContext implements Serializable {

    private static final long serialVersionUID = -1685592780015769516L;

    protected LocalDateTime startTime = LocalDateTime.now();
    private String appKey;
    private String sign;
    private String timestamp;
    /**
     * tenantId.
     */
    private String tenantId;
    /**
     * version.
     */
    private String version;
    /**
     * is rpcType data. now we only support "http","dubbo","sofa".
     */
    private String rpcType;

    /**
     * is method name .
     */
    private String method;
    /**
     * httpMethod .
     */
    private String httpMethod;
    /**
     * 路由id
     */
    private String routeId;
    /**
     * 请求host
     */
    private String host;
    /**
     * 请求ip
     */
    private String ip;
    /**
     * 请求路径
     */
    private String path;
    /**
     * 请求路径
     */
    private String url;

}
