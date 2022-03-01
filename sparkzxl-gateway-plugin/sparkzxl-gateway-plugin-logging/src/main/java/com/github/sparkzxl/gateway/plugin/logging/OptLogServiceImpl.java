package com.github.sparkzxl.gateway.plugin.logging;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.util.HttpRequestUtils;
import com.github.sparkzxl.gateway.plugin.common.condition.data.ParameterDataFactory;
import com.github.sparkzxl.gateway.plugin.common.constant.GatewayConstant;
import com.github.sparkzxl.gateway.plugin.common.constant.ParameterDataConstant;
import com.github.sparkzxl.gateway.plugin.context.GatewayContext;
import com.github.sparkzxl.gateway.plugin.logging.service.IOptLogService;
import com.github.sparkzxl.gateway.plugin.properties.LoggingProperties;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-12-23 18:03
 */
@Slf4j
public class OptLogServiceImpl implements IOptLogService {

    private final LoggingProperties logging;

    public OptLogServiceImpl(LoggingProperties logging) {
        this.logging = logging;
    }

    @Override
    public void recordLog(ServerWebExchange exchange) {
        GatewayContext gatewayContext = exchange.getAttribute(GatewayConstant.GATEWAY_CONTEXT_CONSTANT);
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
        LocalDateTime startTime = gatewayContext.getStartTime();
        ServerHttpRequest request = exchange.getRequest();
        String username = HttpRequestUtils.urlDecode(ParameterDataFactory.builderData(ParameterDataConstant.HEADER, BaseContextConstants.JWT_KEY_NAME, exchange));
        String tenantId = HttpRequestUtils.urlDecode(ParameterDataFactory.builderData(ParameterDataConstant.HEADER, BaseContextConstants.TENANT_ID, exchange));
        logParam.setIp(gatewayContext.getIp())
                .setHost(gatewayContext.getHost())
                .setPath(gatewayContext.getPath())
                .setRouteId(gatewayContext.getRouteId())
                .setUsername(username)
                .setTenantId(tenantId)
                .setHttpMethod(gatewayContext.getHttpMethod())
                .setHttpStatus(exchange.getResponse().getStatusCode().value())
                .setReqTime(startTime)
                .setHeaders(getHeaders(request.getHeaders()))
                .setReqBody(gatewayContext.getRequestBody())
                .setHeaders(gatewayContext.getRequestBody())
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

    private static String getHeaders(final HttpHeaders headers) {
        Map<String,String> headerMap = Maps.newHashMap();
        Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
        entrySet.forEach(entry -> {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            headerMap.put(key,StringUtils.join(value, ","));
        });
        return JsonUtil.toJson(headerMap);
    }

}
