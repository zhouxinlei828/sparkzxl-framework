package com.github.sparkzxl.gateway.filter.sign;

import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.util.DateUtils;
import com.github.sparkzxl.gateway.constant.ExchangeAttributeConstant;
import com.github.sparkzxl.gateway.constant.RequestHeaderConstant;
import com.github.sparkzxl.gateway.properties.VerifySignatureProperties;
import com.github.sparkzxl.gateway.util.ReactorHttpHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

/**
 * description: 系统级输入参数过滤器
 *
 * @author zhouxinlei
 * @date 2021-12-15 19:47
 */
public class SystemRequestParamGatewayFilterFactory extends AbstractGatewayFilterFactory {

    @Autowired
    private VerifySignatureProperties verifySignatureProperties;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = request.getHeaders();
            String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
            if (!validateMediaType(contentType)) {
                return ReactorHttpHelper.errorResponse(response, ResponseInfoStatus.SIGNATURE_NOT_SUPPORTED_EX);
            }
            String timestamp = headers.getFirst(RequestHeaderConstant.X_CA_TIMESTAMP);
            String nonce = headers.getFirst(RequestHeaderConstant.X_CA_NONCE);
            String signType = headers.getFirst(RequestHeaderConstant.X_CA_SIGN_TYPE);
            String signature = headers.getFirst(RequestHeaderConstant.X_CA_SIGNATURE);
            String accessToken = headers.getFirst(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isAnyBlank(timestamp, nonce, signType, signature) ||
                    !StringUtils.isNumeric(timestamp)) {
                return ReactorHttpHelper.errorResponse(response, ResponseInfoStatus.SIGNATURE_EX);
            }
            long timestampL = Long.parseLong(timestamp);
            if (DateUtils.millisecond(LocalDateTime.now()) - timestampL > verifySignatureProperties.getTimestampIntervalSecond()) {
                return ReactorHttpHelper.errorResponse(response, ResponseInfoStatus.REQUEST_TIMESTAMP_EX);
            }
            LinkedHashSet<URI> uris = exchange
                    .getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
            URI uri = uris.iterator().next();

            HttpMethod method = request.getMethod();

            RequestSignParam requestSignParam = RequestSignParam.builder()
                    .path(uri.getPath())
                    .method(method)
                    .nonce(nonce)
                    .signature(signature)
                    .timestamp(Long.valueOf(timestamp))
                    .accessToken(accessToken)
                    .build();

            Map<String, Object> attributeMap = exchange.getAttributes();
            attributeMap.put(ExchangeAttributeConstant.SYSTEM_REQUEST_PARAM, requestSignParam);
            return chain.filter(exchange);
        };
    }

    private boolean validateMediaType(String contentType) {

        return Optional.ofNullable(contentType)
                .filter(StringUtils::isNotBlank)
                .map(MediaType::parseMediaType)
                .map(m -> m.isPresentIn(Arrays.asList(MediaType.APPLICATION_JSON,
                        MediaType.APPLICATION_FORM_URLENCODED,
                        MediaType.MULTIPART_FORM_DATA)))
                .orElse(true);

    }
}
