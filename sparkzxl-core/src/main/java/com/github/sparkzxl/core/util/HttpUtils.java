package com.github.sparkzxl.core.util;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.github.sparkzxl.core.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;
import java.util.Set;

/**
 * description: HTTP工具类
 *
 * @author zhouxinlei
 * @since 2024-04-30 15:19:52
 */
@Slf4j
public class HttpUtils {

    public static String request(String url, String method, Object param, Object body, Map<String, String> headers, int timeout) {
        long start = System.currentTimeMillis();
        HttpResponse httpResponse;
        Method httpMethod = Method.valueOf(method);
        HttpRequest httpRequest;
        switch (httpMethod) {
            case GET:
                url = buildUrlWithQueryParams(url, JsonUtils.getJson().toMap(param));
                log.info("请求 url： {}, method: {}", url, method);
                httpRequest = HttpRequest.get(url)
                        .setReadTimeout(timeout)
                        .setConnectionTimeout(timeout)
                        .headerMap(headers, true);
                httpResponse = httpRequest.execute();
                break;
            case POST:
                String bodyString = JsonUtils.getJson().toJson(body);
                log.info("请求 url：{}, method: {}, body: {}", url, method, bodyString);
                httpRequest = HttpRequest.post(url)
                        .setReadTimeout(timeout)
                        .body(bodyString)
                        .setConnectionTimeout(timeout)
                        .headerMap(headers, true);
                httpResponse = httpRequest.execute();
                break;

            case DELETE:
            case PUT:
                url = buildUrlWithQueryParams(url, JsonUtils.getJson().toMap(param));
                log.info("请求 url：{}, method: {}", url, method);
                httpRequest = HttpRequest.of(url)
                        .method(Method.valueOf(method))
                        .setReadTimeout(timeout)
                        .setConnectionTimeout(timeout)
                        .headerMap(headers, true);
                if (ObjectUtils.isNotEmpty(body)) {
                    httpRequest.body(JsonUtils.getJson().toJson(body));
                }
                httpResponse = httpRequest.execute();
                break;
            default:
                throw new IllegalArgumentException(StrFormatter.format("暂不支持该[{}]请求方式", method));
        }
        if (httpResponse.isOk()) {
            String response = httpResponse.body();
            log.info("响应：{}, 耗时：{} ms", response, (System.currentTimeMillis() - start));
            return response;
        } else {
            throw new RuntimeException(httpResponse.body());
        }
    }

    public static String buildUrlWithQueryParams(String url, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder(url);
        if (params != null && !params.isEmpty()) {
            sb.append("?");
            sb.append(buildQueryParams(params));
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String buildQueryParams(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            Set<Map.Entry<String, Object>> entries = params.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                if (entry.getValue() != null) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue().toString()).append("&");
                }
            }
        }
        return sb.toString();
    }

}
