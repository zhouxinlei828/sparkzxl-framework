package com.github.sparkzxl.web.support;

import cn.hutool.core.convert.Convert;
import com.github.sparkzxl.annotation.response.IgnoreResponseWrap;
import com.github.sparkzxl.annotation.response.Response;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
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
public class ResponseResultAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        final IgnoreResponseWrap[] declaredAnnotationsByType = returnType.getExecutable().getDeclaredAnnotationsByType(IgnoreResponseWrap.class);
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        Response response = (Response) servletRequest.getAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        Boolean supported = ObjectUtils.isNotEmpty(response) && declaredAnnotationsByType.length == 0;
        if (log.isDebugEnabled()) {
            log.debug("判断是否需要全局统一API响应：{}", supported ? "是" : "否");
        }
        return supported;
        // return response != null;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<?
            extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = RequestContextHolderUtils.getResponse();
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        servletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (body instanceof ResponseResult) {
            return body;
        }
        Object returnBody = body;
        int code = ResponseInfoStatus.SUCCESS.getCode();
        String message = ResponseInfoStatus.SUCCESS.getMessage();
        String attribute = (String) RequestContextHolderUtils.getAttribute(BaseContextConstants.EXCEPTION_ATTR_MSG);
        Boolean fallback = Convert.toBool(RequestContextHolderUtils.getAttribute(BaseContextConstants.REMOTE_CALL), Boolean.FALSE);
        if (fallback) {
            code = ResponseInfoStatus.SERVICE_DEGRADATION.getCode();
            message = ResponseInfoStatus.SERVICE_DEGRADATION.getMessage();
            returnBody = null;
        } else if (ObjectUtils.isNotEmpty(attribute)) {
            code = ResponseInfoStatus.FAILURE.getCode();
            message = attribute;
            returnBody = null;
        } else if (returnBody instanceof Boolean && !(Boolean) returnBody) {
            code = ResponseInfoStatus.FAILURE.getCode();
            message = ResponseInfoStatus.FAILURE.getMessage();
        }
        return ResponseResult.result(code, message, returnBody);
    }
}
