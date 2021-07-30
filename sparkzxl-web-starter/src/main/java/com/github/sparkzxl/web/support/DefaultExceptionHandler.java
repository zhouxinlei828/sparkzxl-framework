package com.github.sparkzxl.web.support;

import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.ServiceDegradeException;
import com.github.sparkzxl.core.utils.ResponseResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bouncycastle.openssl.PasswordException;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.NestedServletException;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.SQLException;
import java.util.List;

/**
 * description: 全局异常处理
 *
 * @author zhouxinlei
 */
@ControllerAdvice
@RestController
@Slf4j
public class DefaultExceptionHandler implements Ordered {

    @ExceptionHandler(BizException.class)
    public ApiResult businessException(BizException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("BusinessException：[{}]", e.getMessage());
        int code = e.getCode();
        String message = e.getMessage();
        return ApiResult.apiResult(code, message);
    }

    @ExceptionHandler(NestedServletException.class)
    public ApiResult businessException(NestedServletException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("NestedServletException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.FAILURE);
    }

    @ExceptionHandler(ServiceDegradeException.class)
    public ApiResult serviceDegradeException(ServiceDegradeException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("ServiceDegradeException：[{}]", e.getMessage());
        return ApiResult.apiResult(e.getCode(), e.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("MethodArgumentNotValidException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.PARAM_BIND_ERROR.getCode(), bindingResult(e.getBindingResult()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult illegalArgumentException(IllegalArgumentException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("IllegalArgumentException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.PARAM_TYPE_ERROR.getCode(), e.getMessage());
    }

    private String bindingResult(BindingResult bindingResult) {
        StringBuilder stringBuilder = new StringBuilder();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (CollectionUtils.isNotEmpty(allErrors)) {
            stringBuilder.append(allErrors.get(0).getDefaultMessage() == null ? "" : allErrors.get(0).getDefaultMessage());
        } else {
            stringBuilder.append(ApiResponseStatus.PARAM_BIND_ERROR.getMessage());
        }
        return stringBuilder.toString();
    }

    @ExceptionHandler({AccountNotFoundException.class, PasswordException.class})
    public ApiResult passwordException(Exception e) {
        ResponseResultUtils.clearResponseResult();
        log.error("AccountNotFoundException|PasswordException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.UN_AUTHORIZED.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResult httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("HttpRequestMethodNotSupportedException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult httpMessageNotReadableException(HttpMessageNotReadableException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("HttpMessageNotReadableException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.MSG_NOT_READABLE);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResult notFoundPage404(NoHandlerFoundException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("NoHandlerFoundException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.NOT_FOUND);
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ApiResult httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("HttpMediaTypeNotSupportedException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.MEDIA_TYPE_NOT_SUPPORTED);
    }


    @ExceptionHandler(SQLException.class)
    public ApiResult handleSqlException(SQLException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("SQLException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.SQL_EXCEPTION_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ApiResult handleNullPointerException(NullPointerException e) {
        ResponseResultUtils.clearResponseResult();
        e.printStackTrace();
        return ApiResult.apiResult(ApiResponseStatus.NULL_POINTER_EXCEPTION_ERROR);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 11;
    }
}
