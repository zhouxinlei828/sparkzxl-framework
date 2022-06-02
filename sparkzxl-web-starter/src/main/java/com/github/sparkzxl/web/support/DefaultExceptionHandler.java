package com.github.sparkzxl.web.support;

import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.core.support.ArgumentException;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.ServiceDegradeException;
import com.github.sparkzxl.entity.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.NestedServletException;

import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.ServletException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
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
    public Response<?> businessException(BizException e) {
        log.error("BizException异常:", e);
        return Response.failDetail(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(ArgumentException.class)
    public Response<?> businessException(ArgumentException e) {
        log.error("ArgumentException异常:", e);
        return Response.failDetail(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(NestedServletException.class)
    public Response<?> nestedServletException(NestedServletException e) {
        log.error("NestedServletException 异常:", e);
        return Response.failDetail(ExceptionErrorCode.FAILURE.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<?> servletException(ServletException e) {
        log.warn("ServletException:", e);
        String msg = "UT010016: Not a multi part request";
        if (msg.equalsIgnoreCase(e.getMessage())) {
            return Response.failDetail(ExceptionErrorCode.FILE_UPLOAD_EX);
        }
        return Response.failDetail(ExceptionErrorCode.FAILURE.getErrorCode(), e.getMessage());
    }

    /**
     * jsr 规范中的验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<?> constraintViolationException(ConstraintViolationException ex) {
        log.warn("ConstraintViolationException:", ex);
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        return Response.failDetail(ExceptionErrorCode.PARAM_VALID_ERROR.getErrorCode(), message);
    }

    @ExceptionHandler(ServiceDegradeException.class)
    public Response<?> serviceDegradeException(ServiceDegradeException e) {
        log.error("服务降级:", e);
        return Response.failDetail(e.getErrorCode(), e.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("方法参数无效异常:", e);
        return Response.failDetail(ExceptionErrorCode.PARAM_VALID_ERROR.getErrorCode(),
                bindingResult(e.getBindingResult()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Response<?> illegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException异常:", e);
        return Response.failDetail(ExceptionErrorCode.PARAM_VALID_ERROR);
    }

    @ExceptionHandler(IllegalStateException.class)
    public Response<?> illegalStateException(IllegalStateException e) {
        log.error("IllegalStateException:", e);
        return Response.failDetail(ExceptionErrorCode.PARAM_VALID_ERROR);
    }

    private String bindingResult(BindingResult bindingResult) {
        StringBuilder stringBuilder = new StringBuilder();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (CollectionUtils.isNotEmpty(allErrors)) {
            stringBuilder.append(allErrors.get(0).getDefaultMessage() == null ? "" : allErrors.get(0).getDefaultMessage());
        } else {
            stringBuilder.append(ExceptionErrorCode.PARAM_MISS.getErrorMessage());
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
    public Response<?> bindException(BindException e) {
        log.error("form非法参数验证异常:", e);
        try {
            String msg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
            if (StrUtil.isNotEmpty(msg)) {
                return Response.failDetail(ExceptionErrorCode.PARAM_EX.getErrorCode(), msg);
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
        return Response.failDetail(ExceptionErrorCode.PARAM_EX.getErrorCode(), msg.toString());
    }

    @ExceptionHandler({AccountNotFoundException.class})
    public Response<?> handleAccountNotFoundException(AccountNotFoundException e) {
        log.error("账户找不到异常:", e);
        return Response.failDetail(ExceptionErrorCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response<?> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持异常:", e);
        return Response.failDetail(ExceptionErrorCode.METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response<?> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException异常:", e);
        String prefix = "Could not read document:";
        String message = e.getMessage();
        if (StrUtil.containsAny(message, prefix)) {
            message = String.format("无法正确的解析json类型的参数：%s", StrUtil.subBetween(message, prefix, " at "));
        }
        return Response.failDetail(ExceptionErrorCode.MSG_NOT_READABLE.getErrorCode(), message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Response<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("NoHandlerFoundException异常:", e);
        return Response.failDetail(ExceptionErrorCode.NOT_FOUND.getErrorCode(), e.getMessage());
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Response<?> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("HttpMediaTypeNotSupportedException异常:", e);
        MediaType contentType = e.getContentType();
        if (contentType != null) {
            return Response.failDetail(
                    ExceptionErrorCode.MEDIA_TYPE_NOT_SUPPORTED.getErrorCode(),
                    "请求类型(Content-Type)[" + contentType + "] 与实际接口的请求类型不匹配");
        }
        return Response.failDetail(ExceptionErrorCode.MEDIA_TYPE_NOT_SUPPORTED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Response<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException:", e);
        String msg = "参数：[" + e.getName() + "]的传入值：[" + e.getValue() +
                "]与预期的字段类型：[" + Objects.requireNonNull(e.getRequiredType()).getName() + "]不匹配";
        return Response.failDetail(ExceptionErrorCode.PARAM_TYPE_ERROR.getErrorCode(), msg);
    }

    @ExceptionHandler(NullPointerException.class)
    public Response<?> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException异常:", e);
        return Response.failDetail(ExceptionErrorCode.NULL_POINTER_EXCEPTION_ERROR);
    }

    @ExceptionHandler(MultipartException.class)
    public Response<?> multipartException(MultipartException e) {
        log.error("MultipartException异常:", e);
        return Response.failDetail(ExceptionErrorCode.FILE_UPLOAD_EX);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response<?> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException异常:", e);
        return Response.failDetail(
                ExceptionErrorCode.PARAM_MISS.getErrorCode(),
                "缺少必须的[" + e.getParameterType() + "]类型的参数[" + e.getParameterName() + "]");
    }


    @Override
    public int getOrder() {
        return BeanOrderEnum.BASE_EXCEPTION_ORDER.getOrder();
    }
}
