package com.sparksys.commons.web.component;

import com.sparksys.commons.core.support.ResponseResultStatus;
import com.sparksys.commons.core.base.api.result.ApiResult;
import com.sparksys.commons.web.annotation.ResponseResult;
import com.sparksys.commons.web.constant.WebConstant;
import com.sparksys.commons.web.utils.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description: 判断是否需要返回值包装，如果需要就直接包装
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:41:58
 */
@Slf4j
@ControllerAdvice
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        HttpServletRequest servletRequest = HttpUtils.getRequest();
        ResponseResult responseResult = (ResponseResult) servletRequest.getAttribute(WebConstant.RESPONSE_RESULT_ANN);
        return responseResult != null;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<?
            extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = HttpUtils.getResponse();
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.setContentType("application/json");
        if (body instanceof Boolean) {
            boolean data = (Boolean) body;
            if (!data) {
                return ApiResult.apiResult(ResponseResultStatus.FAILURE, body);
            }
        } else if (body instanceof String) {
            return objectMapper.writeValueAsString(ApiResult.apiResult(ResponseResultStatus.SUCCESS, body));
        }
        return ApiResult.apiResult(ResponseResultStatus.SUCCESS, body);
    }
}
