package com.github.sparkzxl.web.support;

import cn.hutool.core.convert.Convert;
import com.github.sparkzxl.core.base.ResponseError;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.constant.BaseContextConstants;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import com.github.sparkzxl.core.util.RequestContextUtils;
import com.github.sparkzxl.web.annotation.IgnoreResponseWrap;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.sparkzxl.web.annotation.ResponseResult;
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
        final IgnoreResponseWrap[] declaredAnnotationsByType = returnType.getExecutable()
                .getDeclaredAnnotationsByType(IgnoreResponseWrap.class);
        HttpServletRequest servletRequest = RequestContextUtils.getRequest();
        ResponseResult responseResult =
                (ResponseResult) servletRequest.getAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        Boolean supported = ObjectUtils.isNotEmpty(responseResult) && declaredAnnotationsByType.length == 0;
        if (log.isDebugEnabled()) {
            log.debug("判断是否需要全局统一API响应：{}", supported ? "是" : "否");
        }
        return supported;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<?
            extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = RequestContextUtils.getResponse();
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        servletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (body instanceof ApiResult) {
            return body;
        }
        Boolean fallback = Convert.toBool(RequestContextUtils.getAttribute(BaseContextConstants.REMOTE_CALL), Boolean.FALSE);
        int status = servletResponse.getStatus();
        ApiResult<?> result;
        if (fallback) {
            result = ApiResult.fail(ResultErrorCode.SERVICE_DEGRADATION.getErrorCode(), ResultErrorCode.SERVICE_DEGRADATION.getErrorMsg());
        } else if (body instanceof Boolean && !(Boolean) body) {
            result = ApiResult.fail(
                    ResultErrorCode.FAILURE.getErrorCode(), ResultErrorCode.FAILURE.getErrorMsg());
        } else if (status == ResponseError.FAILURE.getCode()) {
            result = ApiResult.fail(
                    ResultErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(), ResultErrorCode.INTERNAL_SERVER_ERROR.getErrorMsg());
            servletResponse.setStatus(ResponseError.SUCCESS.getCode());
        } else {
            result = ApiResult.success(body);
        }
        return result;
    }
}
