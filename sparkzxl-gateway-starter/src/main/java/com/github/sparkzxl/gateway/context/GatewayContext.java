package com.github.sparkzxl.gateway.context;

import com.github.sparkzxl.gateway.properties.LogRequestProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;

/**
 * description: Context Use Cache Request Content
 *
 * @author zhoux
 */
@Getter
@Setter
@ToString
public class GatewayContext {

    public static final String GATEWAY_CONTEXT_CONSTANT = "gatewayContext";

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

    protected LocalDateTime requestDateTime = LocalDateTime.now();

    /**
     * 路由id
     */
    private String routeId;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求路径
     */
    private String url;

    /**
     * 是否输出日志
     */
    private boolean outputLog;

    /**
     * cache json body
     */
    protected String requestBody;
    /**
     * cache Response Body
     */
    protected String responseBody;
    /**
     * request headers
     */
    protected HttpHeaders requestHeaders;
    /**
     * cache form data
     */
    protected MultiValueMap<String, String> formData;

}