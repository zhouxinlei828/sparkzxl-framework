package com.github.sparkzxl.web.support;

import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.core.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.support.ArgumentException;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.JwtParseException;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.NestedServletException;

import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.ServletException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    public ApiResult<?> handleBizException(BizException e) {
        log.error("BizException 异常:", e);
        return ApiResult.fail(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(JwtParseException.class)
    public ApiResult<?> handleJwtParseException(JwtParseException e) {
        log.error("JwtParseException:", e);
        return ApiResult.fail(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(ArgumentException.class)
    public ApiResult<?> handleArgumentException(ArgumentException e) {
        log.warn("ArgumentException 异常:", e);
        return ApiResult.fail(e.getErrorCode(), e.getErrorMsg());
    }

    @ExceptionHandler(NestedServletException.class)
    public ApiResult<?> handleNestedServletException(NestedServletException e) {
        log.error("NestedServletException 异常:", e);
        return ApiResult.fail(ResultErrorCode.FAILURE.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(ServletException.class)
    public ApiResult<?> handleServletException(ServletException e) {
        log.warn("ServletException:", e);
        String msg = "UT010016: Not a multi part request";
        if (msg.equalsIgnoreCase(e.getMessage())) {
            return ApiResult.fail(ResultErrorCode.FILE_UPLOAD_EX);
        }
        return ApiResult.fail(ResultErrorCode.FAILURE.getErrorCode(), e.getMessage());
    }

    /**
     * jsr 规范中的验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult<?> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("ConstraintViolationException:", ex);
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        return ApiResult.fail(ResultErrorCode.PARAM_VALID_ERROR.getErrorCode(), message);
    }

    /**
     * jsr 规范中的验证异常
     */
    @ExceptionHandler(ValidationException.class)
    public ApiResult<?> handleValidationException(ValidationException ex) {
        log.warn("ValidationException:", ex);
        System.out.println(ex.getCause().getMessage());
        return ApiResult.fail(ResultErrorCode.PARAM_VALID_ERROR.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("方法参数无效异常:", e);
        return ApiResult.fail(ResultErrorCode.PARAM_VALID_ERROR.getErrorCode(),
                bindingResult(e.getBindingResult()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException 异常:", e);
        return ApiResult.fail(ResultErrorCode.PARAM_VALID_ERROR);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ApiResult<?> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalStateException:", e);
        return ApiResult.fail(ResultErrorCode.PARAM_VALID_ERROR);
    }

    private String bindingResult(BindingResult bindingResult) {
        StringBuilder stringBuilder = new StringBuilder();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (CollectionUtils.isNotEmpty(allErrors)) {
            stringBuilder.append(allErrors.get(0).getDefaultMessage() == null ? "" : allErrors.get(0).getDefaultMessage());
        } else {
            stringBuilder.append(ResultErrorCode.PARAM_MISS.getErrorMsg());
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
    public ApiResult<?> handleBindException(BindException e) {
        log.warn("form非法参数验证异常:", e);
        try {
            String msg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
            if (StrUtil.isNotEmpty(msg)) {
                return ApiResult.fail(ResultErrorCode.PARAM_EX.getErrorCode(), msg);
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
        return ApiResult.fail(ResultErrorCode.PARAM_EX.getErrorCode(), msg.toString());
    }

    @ExceptionHandler({AccountNotFoundException.class})
    public ApiResult<?> handleAccountNotFoundException(AccountNotFoundException e) {
        log.warn("账户找不到异常:", e);
        return ApiResult.fail(ResultErrorCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持异常:", e);
        return ApiResult.fail(ResultErrorCode.METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException 异常:", e);
        String prefix = "Could not read document:";
        String message = e.getMessage();
        if (StrUtil.containsAny(message, prefix)) {
            message = String.format("无法正确的解析json类型的参数：%s", StrUtil.subBetween(message, prefix, " at "));
        }
        return ApiResult.fail(ResultErrorCode.MSG_NOT_READABLE.getErrorCode(), message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResult<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("NoHandlerFoundException 异常:", e);
        return ApiResult.fail(ResultErrorCode.NOT_FOUND.getErrorCode(), e.getMessage());
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ApiResult<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("HttpMediaTypeNotSupportedException 异常:", e);
        MediaType contentType = e.getContentType();
        if (contentType != null) {
            return ApiResult.fail(
                    ResultErrorCode.MEDIA_TYPE_NOT_SUPPORTED.getErrorCode(),
                    "请求类型(Content-Type)[" + contentType + "] 与实际接口的请求类型不匹配");
        }
        return ApiResult.fail(ResultErrorCode.MEDIA_TYPE_NOT_SUPPORTED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResult<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException:", e);
        String msg = "参数：[" + e.getName() + "]的传入值：[" + e.getValue() +
                "]与预期的字段类型：[" + Objects.requireNonNull(e.getRequiredType()).getName() + "]不匹配";
        return ApiResult.fail(ResultErrorCode.PARAM_TYPE_ERROR.getErrorCode(), msg);
    }

    @ExceptionHandler(NullPointerException.class)
    public ApiResult<?> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException 异常:", e);
        return ApiResult.fail(ResultErrorCode.NULL_POINTER_EXCEPTION_ERROR);
    }

    @ExceptionHandler(MultipartException.class)
    public ApiResult<?> handleMultipartException(MultipartException e) {
        log.error("MultipartException 异常:", e);
        return ApiResult.fail(ResultErrorCode.FILE_UPLOAD_EX);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResult<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException 异常:", e);
        return ApiResult.fail(
                ResultErrorCode.PARAM_MISS.getErrorCode(),
                "缺少必须的[" + e.getParameterType() + "]类型的参数[" + e.getParameterName() + "]");
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.BASE_EXCEPTION_ORDER.getOrder();
    }
}
