package com.github.sparkzxl.gateway.filter.log;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.util.HttpRequestUtils;
import com.github.sparkzxl.gateway.context.GatewayContext;
import com.github.sparkzxl.gateway.properties.LogRequestProperties;
import com.github.sparkzxl.gateway.util.ReactorHttpHelper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-12-23 18:03
 */
@Slf4j
public class OptLogUtil {

    public static void recordLog(ServerWebExchange exchange, LogRequestProperties logging) {
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.GATEWAY_CONTEXT_CONSTANT);
        LogParam logParam = buildLogParam(exchange, gatewayContext);
        log.info("请求日志：IP:{},host:{},httpMethod:{},path:{},timeCost:{}",
                logParam.getIp(),
                logParam.getHost(),
                logParam.getHttpMethod(),
                logParam.getPath(),
                logParam.getTimeCost()
        );
        if (logging.isReadRequestData()) {
            if (StringUtils.isNotBlank(logParam.getQueryParams())) {
                log.info("请求参数：queryParams:{}", logParam.getQueryParams());
            }
            if (StringUtils.isNotBlank(logParam.getReqFormData())) {
                log.info("请求参数：requestFormData:{}", logParam.getReqFormData());
            }
            if (StringUtils.isNotBlank(logParam.getReqBody())) {
                log.info("请求参数：requestBody:{}", logParam.getReqBody());
            }
        }
        if (logging.isReadResponseData()) {
            log.info("请求结果：responseBody:{}", logParam.getRespBody());
        }
    }

    static LogParam buildLogParam(ServerWebExchange exchange, GatewayContext gatewayContext) {
        LogParam logParam = new LogParam();
        LocalDateTime requestDateTime = gatewayContext.getRequestDateTime();
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String username = HttpRequestUtils.urlDecode(ReactorHttpHelper.getHeader(BaseContextConstants.JWT_KEY_NAME, request));
        String tenantId = HttpRequestUtils.urlDecode(ReactorHttpHelper.getHeader(BaseContextConstants.TENANT_ID, request));
        logParam.setIp(ReactorHttpHelper.getIpAddress(request))
                .setUsername(username)
                .setTenantId(tenantId)
                .setHttpMethod(request.getMethod())
                .setHttpStatus(exchange.getResponse().getStatusCode().value())
                .setPath(gatewayContext.getPath())
                .setHost(headers.getFirst(HttpHeaders.HOST))
                .setRouteId(gatewayContext.getRouteId())
                .setReqTime(requestDateTime)
                .setReqBody(gatewayContext.getRequestBody())
                .setTimeCost(StrFormatter.format("{}ms", Duration.between(logParam.getReqTime(), LocalDateTime.now()).toMillis()))
                .setQueryParams(getQueryParams(exchange.getRequest()))
                .setRespBody(Optional.ofNullable(gatewayContext.getResponseBody()).orElse(StringUtils.EMPTY));
        if (MapUtil.isNotEmpty(gatewayContext.getFormData())) {
            logParam.setReqFormData(JsonUtil.toJson(gatewayContext.getFormData()));
        }
        return logParam;
    }

    private static String getQueryParams(final ServerHttpRequest request) {
        MultiValueMap<String, String> params = request.getQueryParams();
        Map<String, String> queryParamMap = Maps.newHashMap();
        if (!params.isEmpty()) {
            params.forEach((key, value) -> {
                queryParamMap.put(key, StringUtils.join(value, ","));
            });
        }
        return JsonUtil.toJson(queryParamMap);
    }

}
