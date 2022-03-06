package com.github.sparkzxl.gateway.plugin.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * description: Context Use gateway Request Content
 *
 * @author zhouxinlei
 * @date 2022-01-10 10:05:12
 */
@Getter
@Setter
@ToString
public class GatewayContext implements Serializable {

    private static final long serialVersionUID = 573721603738759822L;
    protected LocalDateTime startTime = LocalDateTime.now();
    /**
     * cache form data
     */
    protected MultiValueMap<String, String> formData;
    /**
     * cache json body
     */
    protected String requestBody;
    /**
     * cache Response Body
     */
    protected String responseBody;
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
