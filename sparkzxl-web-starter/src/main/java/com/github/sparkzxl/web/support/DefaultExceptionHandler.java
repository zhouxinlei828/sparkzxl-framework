package com.github.sparkzxl.web.support;

import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.entity.response.Response;
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
    public Response<?> businessException(BizException e) {
        log.error("业务异常:", e);
        int code = e.getCode();
        String message = e.getMessage();
        return Response.fail(code, message);
    }

    @ExceptionHandler(NestedServletException.class)
    public Response<?> nestedServletException(NestedServletException e) {
        log.error("NestedServletException 异常:", e);
        return Response.fail(ResponseInfoStatus.FAILURE.getCode(),ResponseInfoStatus.FAILURE.getMessage());
    }

    @ExceptionHandler(ServiceDegradeException.class)
    public Response<?> serviceDegradeException(ServiceDegradeException e) {
        log.error("服务降级:", e);
        return Response.fail(e.getCode(), e.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("方法参数无效异常:", e);
        return Response.fail(ResponseInfoStatus.PARAM_BIND_ERROR.getCode(), bindingResult(e.getBindingResult()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Response<?> illegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException异常:", e);
        return Response.fail(ResponseInfoStatus.ILLEGAL_ARGUMENT_EX.getCode(), e.getMessage());
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
    public Response<?> bindException(BindException e) {
        log.error("form非法参数验证异常:", e);
        try {
            String msg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
            if (StrUtil.isNotEmpty(msg)) {
                return Response.fail(ResponseInfoStatus.PARAM_EX.getCode(), msg);
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
        return Response.fail(ResponseInfoStatus.PARAM_EX.getCode(), msg.toString());
    }

    @ExceptionHandler(PasswordException.class)
    public Response<?> handlePasswordException(PasswordException e) {
        log.error("密码异常:", e);
        return Response.fail(ResponseInfoStatus.PASSWORD_EXCEPTION.getCode(), e.getMessage());
    }

    @ExceptionHandler({AccountNotFoundException.class})
    public Response<?> handleAccountNotFoundException(AccountNotFoundException e) {
        log.error("账户找不到异常:", e);
        return Response.fail(ResponseInfoStatus.ACCOUNT_NOT_FOUND_EXCEPTION.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response<?> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持异常:", e);
        return Response.fail(ResponseInfoStatus.METHOD_NOT_SUPPORTED.getCode(),ResponseInfoStatus.METHOD_NOT_SUPPORTED.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response<?> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException异常:", e);
        String prefix = "Could not read document:";
        String message = e.getMessage();
        if (StrUtil.containsAny(message, prefix)) {
            message = String.format("无法正确的解析json类型的参数：%s", StrUtil.subBetween(message, prefix, " at "));
        }
        return Response.fail(ResponseInfoStatus.MSG_NOT_READABLE.getCode(), message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Response<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("NoHandlerFoundException异常:", e);
        return Response.fail(ResponseInfoStatus.NOT_FOUND.getCode(),ResponseInfoStatus.NOT_FOUND.getMessage());
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Response<?> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("HttpMediaTypeNotSupportedException异常:", e);
        MediaType contentType = e.getContentType();
        if (contentType != null) {
            return Response.fail(ResponseInfoStatus.MEDIA_TYPE_NOT_SUPPORTED.getCode(), "请求类型(Content-Type)[" + contentType + "] 与实际接口的请求类型不匹配");
        }
        return Response.fail(ResponseInfoStatus.MEDIA_TYPE_NOT_SUPPORTED.getCode(),ResponseInfoStatus.MEDIA_TYPE_NOT_SUPPORTED.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public Response<?> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException异常:", e);
        return Response.fail(ResponseInfoStatus.NULL_POINTER_EXCEPTION_ERROR.getCode(),ResponseInfoStatus.NULL_POINTER_EXCEPTION_ERROR.getMessage());
    }

    @ExceptionHandler(MultipartException.class)
    public Response<?> multipartException(MultipartException e) {
        log.error("MultipartException异常:", e);
        return Response.fail(ResponseInfoStatus.REQUIRED_FILE_PARAM_EX.getCode(), ResponseInfoStatus.REQUIRED_FILE_PARAM_EX.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response<?> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException异常:", e);
        return Response.fail(ResponseInfoStatus.ILLEGAL_ARGUMENT_EX.getCode(), "缺少必须的[" + e.getParameterType() + "]类型的参数[" + e.getParameterName() + "]");
    }


    @Override
    public int getOrder() {
        return BeanOrderEnum.BASE_EXCEPTION_ORDER.getOrder();
    }
}
