package com.github.sparkzxl.gateway.context;

import com.github.sparkzxl.gateway.entity.RoutePath;
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
public class CacheGatewayContext {

    public static final String CACHE_GATEWAY_CONTEXT = "cacheGatewayContext";

    protected LocalDateTime requestDateTime = LocalDateTime.now();

    /**
     * 记录路由路径
     */
    private RoutePath routePath;

    /**
     * 是否输出日志
     */
    private boolean outputLog;

    private LogRequestProperties logging;
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
    /**
     * cache all request data include:form data and query param
     */
    protected MultiValueMap<String, String> allRequestData = new LinkedMultiValueMap<>(0);

}