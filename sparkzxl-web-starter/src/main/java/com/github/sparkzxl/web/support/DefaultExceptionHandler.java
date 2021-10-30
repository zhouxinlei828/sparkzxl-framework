package com.github.sparkzxl.web.support;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.ServiceDegradeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bouncycastle.openssl.PasswordException;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.NestedServletException;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Objects;

/**
 * description: 全局异常处理
 *
 * @author zhouxinlei
 */
@ControllerAdvice
@RestController
@Slf4j
@ResponseResultStatus
public class DefaultExceptionHandler implements Ordered {

    @ExceptionHandler(BizException.class)
    public ResponseResult<?> businessException(BizException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        int code = e.getCode();
        String message = e.getMessage();
        return ResponseResult.result(code, message);
    }

    @ExceptionHandler(NestedServletException.class)
    public ResponseResult<?> nestedServletException(NestedServletException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.FAILURE);
    }

    @ExceptionHandler(ServiceDegradeException.class)
    public ResponseResult<?> serviceDegradeException(ServiceDegradeException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(e.getCode(), e.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.PARAM_BIND_ERROR.getCode(), bindingResult(e.getBindingResult()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseResult<?> illegalArgumentException(IllegalArgumentException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.ILLEGAL_ARGUMENT_EX.getCode(), e.getMessage());
    }

    private String bindingResult(BindingResult bindingResult) {
        StringBuilder stringBuilder = new StringBuilder();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (CollectionUtils.isNotEmpty(allErrors)) {
            stringBuilder.append(allErrors.get(0).getDefaultMessage() == null ? "" : allErrors.get(0).getDefaultMessage());
        } else {
            stringBuilder.append(ResponseInfoStatus.PARAM_BIND_ERROR.getMessage());
        }
        return stringBuilder.toString();
    }


    /**
     * form非法参数验证
     *
     * @param e 异常
     * @return CommonResult<?>
     */
    @ExceptionHandler(BindException.class)
    public ResponseResult<?> bindException(BindException e) {
        log.error("form非法参数验证异常:{}", ExceptionUtil.getMessage(e));
        try {
            String msg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
            if (StrUtil.isNotEmpty(msg)) {
                return ResponseResult.result(ResponseInfoStatus.PARAM_EX.getCode(), msg);
            }
        } catch (Exception ee) {
            log.debug("获取异常描述失败", ee);
        }
        StringBuilder msg = new StringBuilder();
        List<FieldError> fieldErrors = e.getFieldErrors();
        fieldErrors.forEach((oe) ->
                msg.append("参数:[").append(oe.getObjectName())
                        .append(".").append(oe.getField())
                        .append("]的传入值:[").append(oe.getRejectedValue()).append("]与预期的字段类型不匹配.")
        );
        return ResponseResult.result(ResponseInfoStatus.PARAM_EX.getCode(), msg.toString());
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseResult<?> handlePasswordException(PasswordException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.PASSWORD_EXCEPTION.getCode(), e.getMessage());
    }

    @ExceptionHandler({AccountNotFoundException.class})
    public ResponseResult<?> handleAccountNotFoundException(AccountNotFoundException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.ACCOUNT_NOT_FOUND_EXCEPTION.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseResult<?> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseResult<?> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        String message = e.getMessage();
        if (StrUtil.containsAny(message, "Could not read document:")) {
            message = String.format("无法正确的解析json类型的参数：%s", StrUtil.subBetween(message, "Could not read document:", " at "));
        }
        return ResponseResult.result(ResponseInfoStatus.MSG_NOT_READABLE.getCode(), message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseResult<?> notFoundPage404(NoHandlerFoundException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.NOT_FOUND);
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseResult<?> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        MediaType contentType = e.getContentType();
        if (contentType != null) {
            return ResponseResult.result(ResponseInfoStatus.MEDIA_TYPE_NOT_SUPPORTED.getCode(), "请求类型(Content-Type)[" + contentType + "] 与实际接口的请求类型不匹配");
        }
        return ResponseResult.result(ResponseInfoStatus.MEDIA_TYPE_NOT_SUPPORTED);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseResult<?> handleNullPointerException(NullPointerException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.NULL_POINTER_EXCEPTION_ERROR);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseResult<?> multipartException(MultipartException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.REQUIRED_FILE_PARAM_EX.getCode(), ResponseInfoStatus.REQUIRED_FILE_PARAM_EX.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseResult<?> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.ILLEGAL_ARGUMENT_EX.getCode(), "缺少必须的[" + e.getParameterType() + "]类型的参数[" + e.getParameterName() + "]");
    }


    @Override
    public int getOrder() {
        return BeanOrderEnum.BASE_EXCEPTION_ORDER.getOrder();
    }
}
