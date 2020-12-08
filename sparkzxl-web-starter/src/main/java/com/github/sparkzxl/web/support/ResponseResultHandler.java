package com.github.sparkzxl.web.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sparkzxl.core.constant.CoreConstant;
import com.github.sparkzxl.core.support.ResponseResultStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import com.github.sparkzxl.web.annotation.ResponseResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
import java.nio.charset.StandardCharsets;

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
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        ResponseResult responseResult = (ResponseResult) servletRequest.getAttribute(CoreConstant.RESPONSE_RESULT_ANN);
        return responseResult != null;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<?
            extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        Object returnBody = body;
        HttpServletResponse servletResponse = RequestContextHolderUtils.getResponse();
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        servletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        int code = ResponseResultStatus.SUCCESS.getCode();
        String message = ResponseResultStatus.SUCCESS.getMessage();
        String returnTypeName = returnType.getGenericParameterType().getTypeName();
        String attribute = (String) RequestContextHolderUtils.getAttribute(CoreConstant.EXCEPTION_ATTR_MSG);
        if (ObjectUtils.isNotEmpty(RequestContextHolderUtils.getAttribute(CoreConstant.FALLBACK))) {
            code = ResponseResultStatus.SERVICE_DEGRADATION.getCode();
            message = ResponseResultStatus.SERVICE_DEGRADATION.getMessage();
        } else if (ObjectUtils.isNotEmpty(attribute)) {
            code = ResponseResultStatus.FAILURE.getCode();
            message = attribute;
            returnBody = null;
        } else if (returnBody instanceof Boolean && !(Boolean) returnBody) {
            code = ResponseResultStatus.FAILURE.getCode();
            message = ResponseResultStatus.FAILURE.getMessage();
        }
        if (returnTypeName.equals(String.class.getTypeName())) {
            return objectMapper.writeValueAsString(ApiResult.apiResult(code,message,returnBody));
        }
        return ApiResult.apiResult(code,message,returnBody);
    }
}
