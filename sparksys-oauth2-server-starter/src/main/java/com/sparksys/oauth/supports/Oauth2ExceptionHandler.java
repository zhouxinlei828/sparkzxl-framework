package com.sparksys.oauth.supports;

import com.sparksys.core.base.result.ApiResult;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * description: oauth 异常处理
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 12:42:19
 */
@ControllerAdvice
public class Oauth2ExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public ApiResult handleOauth2(OAuth2Exception e) {
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

}
