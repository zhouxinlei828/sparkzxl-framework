package com.github.sparkzxl.gateway.plugin.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.base.result.Response;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ReactorHttpHelper {

    public static String getHeader(ServerHttpRequest request, String headerName) {
        HttpHeaders headers = request.getHeaders();
        String val = StrUtil.EMPTY;
        if (headers.isEmpty()) {
            return val;
        }

        List<String> headerList = headers.get(headerName);
        if (headerList == null || headerList.isEmpty()) {
            return val;
        }
        val = headerList.get(0);

        if (StringUtils.isNotBlank(val)) {
            return val;
        }
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        if (queryParams.isEmpty()) {
            return val;
        }
        return queryParams.getFirst(headerName);
    }


    public static String formatHeader(Object str) {
        String valueStr = str.toString();
        return URLUtil.encode(valueStr);
    }


    public static Mono<Void> error(ServerHttpResponse response, String code, String message) {
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        byte[] bytes = JSON.toJSONString(Response.fail(code, message)).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Flux.just(buffer));
    }

    public static Mono<Void> error(ServerHttpResponse response, ResultErrorCode resultErrorCode) {
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        byte[] bytes = JSON.toJSONString(Response.fail(resultErrorCode)).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Flux.just(buffer));
    }
}
