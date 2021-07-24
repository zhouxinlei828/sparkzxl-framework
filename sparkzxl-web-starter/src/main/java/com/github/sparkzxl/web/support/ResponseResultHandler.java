package com.github.sparkzxl.web.support;

import com.github.sparkzxl.annotation.result.ResponseResult;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
 */
@Slf4j
@ControllerAdvice
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        ResponseResult responseResult = (ResponseResult) servletRequest.getAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        return responseResult != null;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<?
            extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = RequestContextHolderUtils.getResponse();
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        servletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (body instanceof ApiResult) {
            return body;
        }
        Object returnBody = body;
        int code = ApiResponseStatus.SUCCESS.getCode();
        String message = ApiResponseStatus.SUCCESS.getMessage();
        String attribute = (String) RequestContextHolderUtils.getAttribute(BaseContextConstants.EXCEPTION_ATTR_MSG);
        if (ObjectUtils.isNotEmpty(RequestContextHolderUtils.getAttribute(BaseContextConstants.FALLBACK))) {
            code = ApiResponseStatus.SERVICE_DEGRADATION.getCode();
            message = ApiResponseStatus.SERVICE_DEGRADATION.getMessage();
            returnBody = null;
        } else if (ObjectUtils.isNotEmpty(attribute)) {
            code = ApiResponseStatus.FAILURE.getCode();
            message = attribute;
            returnBody = null;
        } else if (returnBody instanceof Boolean && !(Boolean) returnBody) {
            code = ApiResponseStatus.FAILURE.getCode();
            message = ApiResponseStatus.FAILURE.getMessage();
        }
        return ApiResult.apiResult(code, message, returnBody);
    }
}
