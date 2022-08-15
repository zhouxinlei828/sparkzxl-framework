package com.github.sparkzxl.gateway.plugin.dubbo.entity;

import lombok.Data;

import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-08-13 12:15:38
 */
@Data
public class HttpRequestInfo {

    Map<String, String> queryParams;

    Map<String, String> headers;

    Map<String, String> requestBody;

    Map<String, String> formData;

}
