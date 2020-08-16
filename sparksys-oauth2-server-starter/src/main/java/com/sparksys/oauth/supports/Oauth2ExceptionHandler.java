package com.sparksys.oauth.supports;

import com.sparksys.core.base.result.ApiResult;
import com.sparksys.core.constant.CoreConstant;
import com.sparksys.core.utils.RequestContextHolderUtils;
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
        servletRequest.removeAttribute(CoreConstant.RESPONSE_RESULT_ANN);
    }

    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public ApiResult handleOauth2(OAuth2Exception e) {
        handleResponseResult();
        return ApiResult.apiResult(e.getHttpErrorCode(), e.getMessage());
    }

}
