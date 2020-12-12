package com.github.sparkzxl.oauth.supports;

import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * description: oauth 异常处理
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 12:42:19
 */
@ControllerAdvice
public class Oauth2ExceptionHandler {

    public void handleResponseResult() {
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        servletRequest.removeAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
    }

    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public ApiResult handleOauth2(OAuth2Exception e) {
        handleResponseResult();
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

}
