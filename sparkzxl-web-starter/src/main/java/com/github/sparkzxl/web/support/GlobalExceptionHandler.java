package com.github.sparkzxl.web.support;

import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.ServiceDegradeException;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bouncycastle.openssl.PasswordException;
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
import javax.servlet.http.HttpServletRequest;
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
public class GlobalExceptionHandler {

    public void handleResponseResult() {
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        com.github.sparkzxl.core.annotation.ResponseResult responseResult = (com.github.sparkzxl.core.annotation.ResponseResult) servletRequest.getAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        boolean result = responseResult != null;
        if (result) {
            servletRequest.removeAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        }
    }

    @ExceptionHandler(BizException.class)
    public ResponseResult businessException(BizException e) {
        handleResponseResult();
        log.error("BusinessException：[{}]", e.getMessage());
        int code = e.getCode();
        String message = e.getMessage();
        return ResponseResult.apiResult(code, message);
    }

    @ExceptionHandler(NestedServletException.class)
    public ResponseResult businessException(NestedServletException e) {
        handleResponseResult();
        log.error("NestedServletException：[{}]", e.getMessage());
        return ResponseResult.apiResult(ApiResponseStatus.FAILURE);
    }

    @ExceptionHandler(ServiceDegradeException.class)
    public ResponseResult serviceDegradeException(ServiceDegradeException e) {
        handleResponseResult();
        log.error("ServiceDegradeException：[{}]", e.getMessage());
        int code = e.getCode();
        String message = e.getMessage();
        return ResponseResult.apiResult(code, message);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
        handleResponseResult();
        log.error("MethodArgumentNotValidException：[{}]", e.getMessage());
        return ResponseResult.apiResult(ApiResponseStatus.PARAM_BIND_ERROR.getCode(), bindingResult(e.getBindingResult()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseResult illegalArgumentException(IllegalArgumentException e) {
        handleResponseResult();
        log.error("IllegalArgumentException：[{}]", e.getMessage());
        return ResponseResult.apiResult(ApiResponseStatus.PARAM_TYPE_ERROR);
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
    public ResponseResult passwordException(Exception e) {
        handleResponseResult();
        log.error("AccountNotFoundException|PasswordException：[{}]", e.getMessage());
        return ResponseResult.apiResult(ApiResponseStatus.UN_AUTHORIZED.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseResult httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        handleResponseResult();
        log.error("HttpRequestMethodNotSupportedException：[{}]", e.getMessage());
        return ResponseResult.apiResult(ApiResponseStatus.METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseResult httpMessageNotReadableException(HttpMessageNotReadableException e) {
        handleResponseResult();
        log.error("HttpMessageNotReadableException：[{}]", e.getMessage());
        return ResponseResult.apiResult(ApiResponseStatus.MSG_NOT_READABLE);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseResult notFoundPage404(NoHandlerFoundException e) {
        handleResponseResult();
        log.error("NoHandlerFoundException：[{}]", e.getMessage());
        return ResponseResult.apiResult(ApiResponseStatus.NOT_FOUND);
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseResult httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        handleResponseResult();
        log.error("HttpMediaTypeNotSupportedException：[{}]", e.getMessage());
        return ResponseResult.apiResult(ApiResponseStatus.MEDIA_TYPE_NOT_SUPPORTED);
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public ResponseResult handleSqlException(SQLException e) {
        handleResponseResult();
        log.error("SQLException：[{}]", e.getMessage());
        return ResponseResult.apiResult(ApiResponseStatus.SQL_EXCEPTION_ERROR);
    }

}
