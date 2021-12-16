package com.github.sparkzxl.gateway.context;

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

    public static final String CACHE_GATEWAY_CONTEXT = "cacheGatewayContext";

    protected LocalDateTime requestDateTime = LocalDateTime.now();

    /**
     * whether read request data
     */
    protected boolean readRequestData;
    /**
     * whether read response data
     */
    protected boolean readResponseData;
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
    private String routeId;
    /**
     * whether log request data
     */
    private boolean logRequest = false;

}