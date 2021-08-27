package com.github.sparkzxl.sentinel.support;


import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.annotation.result.ResponseResult;
import com.github.sparkzxl.constant.AppContextConstants;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * description: 全局异常处理
 *
 * @author zhouxinlei
 */
@ControllerAdvice
@ResponseResultStatus
@RestController
@Slf4j
public class SentinelExceptionHandler implements Ordered {

    public void handleResponseResult() {
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        ResponseResult responseResult = (ResponseResult) servletRequest.getAttribute(AppContextConstants.RESPONSE_RESULT_ANN);
        boolean result = responseResult != null;
        if (result) {
            servletRequest.removeAttribute(AppContextConstants.RESPONSE_RESULT_ANN);
        }
    }

    @ExceptionHandler(value = FlowException.class)
    public ApiResult<?> blockExceptionHandler(FlowException e) {
        handleResponseResult();
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.REQ_LIMIT, e.getMessage());
    }

    @ExceptionHandler(value = AuthorityException.class)
    public ApiResult<?> blockExceptionHandler(AuthorityException e) {
        handleResponseResult();
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.REQ_BLACKLIST, e.getMessage());
    }

    @ExceptionHandler(value = SystemBlockException.class)
    public ApiResult<?> blockExceptionHandler(SystemBlockException e) {
        handleResponseResult();
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.SYSTEM_BLOCK, e.getMessage());
    }

    @ExceptionHandler(value = ParamFlowException.class)
    public ApiResult<?> blockExceptionHandler(ParamFlowException e) {
        handleResponseResult();
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.PARAM_FLOW, e.getMessage());
    }

    @ExceptionHandler(value = DegradeException.class)
    public ApiResult<?> blockExceptionHandler(DegradeException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.SERVICE_DEGRADATION);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 13;
    }
}
