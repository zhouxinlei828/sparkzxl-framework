package com.github.sparkzxl.gateway.filter.log;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.util.HttpRequestUtils;
import com.github.sparkzxl.gateway.context.CacheGatewayContext;
import com.github.sparkzxl.gateway.entity.RoutePath;
import com.github.sparkzxl.gateway.properties.LogRequestProperties;
import com.github.sparkzxl.gateway.util.ReactorHttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-12-23 18:03
 */
@Slf4j
public class OptLogUtil {

    public static void recordLog(ServerWebExchange exchange) {
        CacheGatewayContext cacheGatewayContext = exchange.getAttribute(CacheGatewayContext.CACHE_GATEWAY_CONTEXT);
        LogParam logParam = buildLogParam(exchange, cacheGatewayContext);
        log.info("请求日志：IP:{},host:{},httpMethod:{},path:{},timeCost:{}",
                logParam.getIp(),
                logParam.getHost(),
                logParam.getHttpMethod(),
                logParam.getPath(),
                logParam.getTimeCost()
        );
        LogRequestProperties logging = cacheGatewayContext.getLogging();
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

    static LogParam buildLogParam(ServerWebExchange exchange, CacheGatewayContext cacheGatewayContext) {
        LogParam logParam = new LogParam();
        RoutePath routePath = cacheGatewayContext.getRoutePath();
        LocalDateTime requestDateTime = cacheGatewayContext.getRequestDateTime();
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        HttpHeaders headers = request.getHeaders();
        String username = HttpRequestUtils.urlDecode(ReactorHttpHelper.getHeader(BaseContextConstants.JWT_KEY_NAME, request));
        String tenantId = HttpRequestUtils.urlDecode(ReactorHttpHelper.getHeader(BaseContextConstants.TENANT_ID, request));
        logParam.setIp(ReactorHttpHelper.getIpAddress(request))
                .setUsername(username)
                .setTenantId(tenantId)
                .setHttpMethod(request.getMethod())
                .setHttpStatus(exchange.getResponse().getStatusCode().value())
                .setPath(uri.getPath())
                .setHost(headers.getFirst(HttpHeaders.HOST))
                .setRouteId(routePath.getRouteId())
                .setRouterToUri(routePath.getUrl())
                .setReqTime(requestDateTime)
                .setReqBody(cacheGatewayContext.getRequestBody())
                .setTimeCost(StrFormatter.format("{}ms", Duration.between(logParam.getReqTime(), LocalDateTime.now()).toMillis()))
                .setRespBody(Optional.ofNullable(cacheGatewayContext.getResponseBody()).orElse(StringUtils.EMPTY));

        if (MapUtil.isNotEmpty(cacheGatewayContext.getAllRequestData())) {
            logParam.setQueryParams(JsonUtil.toJson(cacheGatewayContext.getAllRequestData()));
        }
        if (MapUtil.isNotEmpty(cacheGatewayContext.getFormData())) {
            logParam.setQueryParams(JsonUtil.toJson(cacheGatewayContext.getFormData()));
        }
        return logParam;
    }

}
