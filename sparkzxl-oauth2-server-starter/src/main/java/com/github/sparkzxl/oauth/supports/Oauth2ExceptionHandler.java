package com.github.sparkzxl.oauth.supports;

import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * description: oauth 异常处理
 *
 * @author zhouxinlei
 */
@ControllerAdvice
@RestController
@Slf4j
public class Oauth2ExceptionHandler {

    public void handleResponseResult() {
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        servletRequest.removeAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
    }

    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public ApiResult handleOauth2(OAuth2Exception e) {
        handleResponseResult();
        log.error("OAuth2Exception：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = BadClientCredentialsException.class)
    public ApiResult handleBadClientCredentialsException(BadClientCredentialsException e) {
        handleResponseResult();
        log.error("BadClientCredentialsException：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = ClientAuthenticationException.class)
    public ApiResult handleClientAuthenticationException(ClientAuthenticationException e) {
        handleResponseResult();
        log.error("ClientAuthenticationException：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = InsufficientScopeException.class)
    public ApiResult handleInsufficientScopeException(InsufficientScopeException e) {
        handleResponseResult();
        log.error("InsufficientScopeException：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = InvalidClientException.class)
    public ApiResult handleInvalidClientException(InvalidClientException e) {
        handleResponseResult();
        log.error("InvalidClientException：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = InvalidGrantException.class)
    public ApiResult handleInvalidGrantException(InvalidGrantException e) {
        handleResponseResult();
        log.error("InvalidGrantException：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }


    @ResponseBody
    @ExceptionHandler(value = InvalidRequestException.class)
    public ApiResult handleInvalidRequestException(InvalidRequestException e) {
        handleResponseResult();
        log.error("InvalidRequestException：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = InvalidScopeException.class)
    public ApiResult handleInvalidScopeException(InvalidScopeException e) {
        handleResponseResult();
        log.error("InvalidScopeException：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = InvalidTokenException.class)
    public ApiResult handleInvalidTokenException(InvalidTokenException e) {
        handleResponseResult();
        log.error("InvalidTokenException：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), "token校验失败");
    }

    @ResponseBody
    @ExceptionHandler(value = RedirectMismatchException.class)
    public ApiResult handleRedirectMismatchException(RedirectMismatchException e) {
        handleResponseResult();
        log.error("RedirectMismatchException：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = SerializationException.class)
    public ApiResult handleSerializationException(SerializationException e) {
        handleResponseResult();
        log.error("SerializationException：{}", e.getMessage());
        return ApiResult.apiResult(500, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = UnapprovedClientAuthenticationException.class)
    public ApiResult handleUnapprovedClientAuthenticationException(UnapprovedClientAuthenticationException e) {
        handleResponseResult();
        log.error("UnapprovedClientAuthenticationException：{}", e.getMessage());
        return ApiResult.apiResult(500, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = UnauthorizedClientException.class)
    public ApiResult handleUnapprovedClientAuthenticationException(UnauthorizedClientException e) {
        handleResponseResult();
        log.error("UnauthorizedClientException：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = UnsupportedResponseTypeException.class)
    public ApiResult handleUnsupportedResponseTypeException(UnsupportedResponseTypeException e) {
        handleResponseResult();
        log.error("UnsupportedResponseTypeException：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = UnsupportedGrantTypeException.class)
    public ApiResult handleUnapprovedClientAuthenticationException(UnsupportedGrantTypeException e) {
        handleResponseResult();
        log.error("UnsupportedGrantTypeException：{}", e.getMessage());
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

}
