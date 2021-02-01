package com.github.sparkzxl.web.support;

import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.support.BusinessException;
import com.github.sparkzxl.core.support.ResponseResultStatus;
import com.github.sparkzxl.core.support.ServiceDegradeException;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import com.github.sparkzxl.web.annotation.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.openssl.PasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * description: 全局异常处理
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:44:48
 */
@ControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler {

    public void handleResponseResult() {
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        ResponseResult responseResult = (ResponseResult) servletRequest.getAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        boolean result = responseResult != null;
        if (result) {
            servletRequest.removeAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        }
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResult businessException(BusinessException e) {
        handleResponseResult();
        log.error("BusinessException：{}", e.getMessage());
        int code = e.getCode();
        String message = e.getMessage();
        return ApiResult.apiResult(code, message);
    }

    @ExceptionHandler(NestedServletException.class)
    public ApiResult businessException(NestedServletException e) {
        handleResponseResult();
        log.error("NestedServletException：{}", e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.FAILURE);
    }

    @ExceptionHandler(ServiceDegradeException.class)
    public ApiResult serviceDegradeException(ServiceDegradeException e) {
        handleResponseResult();
        log.error("ServiceDegradeException：{}", e.getMessage());
        int code = e.getCode();
        String message = e.getMessage();
        return ApiResult.apiResult(code, message);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
        handleResponseResult();
        log.error("MethodArgumentNotValidException：{}", e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.PARAM_BIND_ERROR.getCode(), bindingResult(e.getBindingResult()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult illegalArgumentException(IllegalArgumentException e) {
        handleResponseResult();
        log.error("IllegalArgumentException：{}", e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.PARAM_TYPE_ERROR);
    }

    private String bindingResult(BindingResult bindingResult) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ObjectError objectError : bindingResult.getAllErrors()) {
            stringBuilder.append(", ");
            if (objectError instanceof FieldError) {
                stringBuilder.append(((FieldError) objectError).getField()).append(": ");
            }
            stringBuilder.append(objectError.getDefaultMessage() == null ? "" : objectError.getDefaultMessage());
        }
        return stringBuilder.substring(2);
    }

    @ExceptionHandler({AccountNotFoundException.class, PasswordException.class})
    public ApiResult passwordException(Exception e) {
        handleResponseResult();
        log.error("AccountNotFoundException|PasswordException：{}", e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.UN_AUTHORIZED.getCode(), e.getMessage());
    }

    /**
     * 405
     *
     * @param
     * @return ApiResult
     * @author zhouxinlei
     * @date 2019/5/25 0025
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResult httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        handleResponseResult();
        log.error("HttpRequestMethodNotSupportedException：{}", e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult httpMessageNotReadableException(HttpMessageNotReadableException e) {
        handleResponseResult();
        log.error("HttpMessageNotReadableException：{}", e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.MSG_NOT_READABLE);
    }

    /**
     * 404 没有找到访问资源
     *
     * @param
     * @return ApiResult
     * @author zhouxinlei
     * @date 2019/5/25 0025
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResult notFoundPage404(NoHandlerFoundException e) {
        handleResponseResult();
        log.error("NoHandlerFoundException：{}", e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.NOT_FOUND);
    }

    /**
     * 415 不支持媒体类型
     *
     * @param e 异常
     * @return ApiResult
     * @author zhouxinlei
     * @date 2019/5/25 0025
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ApiResult httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        handleResponseResult();
        log.error("HttpMediaTypeNotSupportedException：{}", e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.MEDIA_TYPE_NOT_SUPPORTED);
    }

    /**
     * 数据库异常
     *
     * @param e 数据库异常
     * @return ApiResult
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public ApiResult handleSQLException(SQLException e) {
        handleResponseResult();
        log.error("SQLException：{}", e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.SQL_EXCEPTION_ERROR);
    }

}
