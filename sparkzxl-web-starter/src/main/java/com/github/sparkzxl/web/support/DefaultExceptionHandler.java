package com.github.sparkzxl.web.support;

import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.core.base.HttpCode;
import com.github.sparkzxl.core.base.result.R;
import com.github.sparkzxl.core.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.support.*;
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
import java.net.UnknownHostException;
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
    public R<?> handleBizException(BizException e) {
        log.error("BizException 异常:", e);
        return R.failDetail(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(JwtParseException.class)
    public R<?> handleJwtParseException(JwtParseException e) {
        log.error("JwtParseException:", e);
        return R.failDetail(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(ArgumentException.class)
    public R<?> handleArgumentException(ArgumentException e) {
        log.warn("ArgumentException 异常:", e);
        return R.failDetail(e.getErrorCode(), e.getErrorMsg());
    }

    @ExceptionHandler(NestedServletException.class)
    public R<?> handleNestedServletException(NestedServletException e) {
        log.error("NestedServletException 异常:", e);
        return R.failDetail(ResultErrorCode.FAILURE.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(ServletException.class)
    public R<?> handleServletException(ServletException e) {
        log.warn("ServletException:", e);
        String msg = "UT010016: Not a multi part request";
        if (msg.equalsIgnoreCase(e.getMessage())) {
            return R.fail(ResultErrorCode.FILE_UPLOAD_EX);
        }
        return R.failDetail(ResultErrorCode.FAILURE.getErrorCode(), e.getMessage());
    }

    /**
     * jsr 规范中的验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<?> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("ConstraintViolationException:", ex);
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        return R.failDetail(ResultErrorCode.PARAM_VALID_ERROR.getErrorCode(), message);
    }

    /**
     * jsr 规范中的验证异常
     */
    @ExceptionHandler(ValidationException.class)
    public R<?> handleValidationException(ValidationException ex) {
        log.warn("ValidationException:", ex);
        System.out.println(ex.getCause().getMessage());
        return R.failDetail(ResultErrorCode.PARAM_VALID_ERROR.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("方法参数无效异常:", e);
        return R.failDetail(ResultErrorCode.PARAM_VALID_ERROR.getErrorCode(),
                bindingResult(e.getBindingResult()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public R<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException 异常:", e);
        return R.fail(ResultErrorCode.PARAM_VALID_ERROR);
    }

    @ExceptionHandler(IllegalStateException.class)
    public R<?> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalStateException:", e);
        return R.fail(ResultErrorCode.PARAM_VALID_ERROR);
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
    public R<?> handleBindException(BindException e) {
        log.warn("form非法参数验证异常:{}", e.getMessage());
        try {
            String msg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
            if (StrUtil.isNotEmpty(msg)) {
                return R.failDetail(ResultErrorCode.PARAM_EX.getErrorCode(), msg);
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
        return R.failDetail(ResultErrorCode.PARAM_EX.getErrorCode(), msg.toString());
    }

    @ExceptionHandler({AccountNotFoundException.class})
    public R<?> handleAccountNotFoundException(AccountNotFoundException e) {
        log.warn("AccountNotFoundException异常:{}", e.getMessage());
        return R.fail(ResultErrorCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持异常:{}", e.getMessage());
        return R.fail(ResultErrorCode.METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException 异常:", e);
        String prefix = "Could not read document:";
        String message = e.getMessage();
        if (StrUtil.containsAny(message, prefix)) {
            message = String.format("无法正确的解析json类型的参数：%s", StrUtil.subBetween(message, prefix, " at "));
        }
        return R.failDetail(ResultErrorCode.MSG_NOT_READABLE.getErrorCode(), message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public R<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("NoHandlerFoundException 异常:{}", e.getMessage());
        return R.failDetail(ResultErrorCode.NOT_FOUND.getErrorCode(), e.getMessage());
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public R<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("HttpMediaTypeNotSupportedException 异常:{}", e.getMessage());
        MediaType contentType = e.getContentType();
        if (contentType != null) {
            return R.failDetail(
                    ResultErrorCode.MEDIA_TYPE_NOT_SUPPORTED.getErrorCode(),
                    "请求类型(Content-Type)[" + contentType + "] 与实际接口的请求类型不匹配");
        }
        return R.fail(ResultErrorCode.MEDIA_TYPE_NOT_SUPPORTED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException:", e);
        String msg = "参数：[" + e.getName() + "]的传入值：[" + e.getValue() +
                "]与预期的字段类型：[" + Objects.requireNonNull(e.getRequiredType()).getName() + "]不匹配";
        return R.failDetail(ResultErrorCode.PARAM_TYPE_ERROR.getErrorCode(), msg);
    }

    @ExceptionHandler(NullPointerException.class)
    public R<?> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException 异常:", e);
        return R.fail(ResultErrorCode.NULL_POINTER_EXCEPTION_ERROR);
    }

    @ExceptionHandler(MultipartException.class)
    public R<?> handleMultipartException(MultipartException e) {
        log.error("MultipartException 异常:", e);
        return R.fail(ResultErrorCode.FILE_UPLOAD_EX);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException 异常:", e);
        return R.failDetail(
                ResultErrorCode.PARAM_MISS.getErrorCode(),
                "缺少必须的[" + e.getParameterType() + "]类型的参数[" + e.getParameterName() + "]");
    }

    @ExceptionHandler(TokenExpireException.class)
    public R<?> handleLoginExpireException(TokenExpireException e) {
        log.error("TokenExpireException 异常:{}", e.getMessage());
        return R.fail(HttpCode.UNAUTHORIZED, e.getErrorCode(), e.getErrorMsg());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public R<?> handleUserNotFoundException(UserNotFoundException e) {
        log.error("UserNotFoundException 异常:{}", e.getMessage());
        return R.fail(HttpCode.UNAUTHORIZED, e.getErrorCode(), e.getErrorMsg());
    }

    @ExceptionHandler(UserPasswordErrorException.class)
    public R<?> handleUserPasswordErrorException(UserPasswordErrorException e) {
        log.error("UserPasswordErrorException 异常:{}", e.getMessage());
        return R.fail(HttpCode.UNAUTHORIZED, e.getErrorCode(), e.getErrorMsg());
    }

    @ExceptionHandler(UnknownHostException.class)
    public R<?> handleUnknownHostException(UnknownHostException e) {
        log.warn("UnknownHostException:{}", e.getMessage());
        return R.failDetail(ResultErrorCode.IP_OR_DOMAIN_NAME_UNREACHABLE.getErrorCode(), ResultErrorCode.IP_OR_DOMAIN_NAME_UNREACHABLE.getErrorMsg());
    }

    @ExceptionHandler(LimitException.class)
    public R<?> handleLimitException(LimitException e) {
        log.warn("LimitException:{}", e.getErrorMsg());
        return R.failDetail(ResultErrorCode.REQ_LIMIT.getErrorCode(), e.getErrorMsg());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.BASE_EXCEPTION_ORDER.getOrder();
    }
}
