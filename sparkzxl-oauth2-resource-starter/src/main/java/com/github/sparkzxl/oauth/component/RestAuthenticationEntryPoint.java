package com.github.sparkzxl.oauth.component;

import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.support.ResponseResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * description: 当未登录或者token失效访问接口时，自定义的返回结果
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:35:00
 */
@Slf4j
public class RestAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        log.error("AuthenticationException：{}", e.getMessage());
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        String body = JSONUtil.toJsonStr(ApiResult.apiResult(ResponseResultStatus.UN_AUTHORIZED.getCode(), e.getMessage()));
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

}
