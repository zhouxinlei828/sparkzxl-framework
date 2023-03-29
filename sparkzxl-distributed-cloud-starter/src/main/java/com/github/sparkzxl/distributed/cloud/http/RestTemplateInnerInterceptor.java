package com.github.sparkzxl.distributed.cloud.http;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * description: rest请求内部拦截器
 *
 * @author zhouxinlei
 * @since 2023-03-29 16:19:51
 */
public interface RestTemplateInnerInterceptor {

    default void beforeExecute(HttpRequest request) {

    }

    default void afterExecute(ClientHttpResponse response) {

    }
}
