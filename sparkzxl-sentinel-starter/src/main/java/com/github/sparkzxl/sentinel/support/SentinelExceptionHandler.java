package com.github.sparkzxl.sentinel.support;


import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.github.sparkzxl.annotation.result.ResponseResult;
import com.github.sparkzxl.constant.BaseContextConstants;
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
@RestController
@Slf4j
public class SentinelExceptionHandler implements Ordered {

    public void handleResponseResult() {
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        ResponseResult responseResult = (ResponseResult) servletRequest.getAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        boolean result = responseResult != null;
        if (result) {
            servletRequest.removeAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        }
    }

    @ExceptionHandler(value = FlowException.class)
    public ApiResult<?> blockExceptionHandler(FlowException flowException) {
        handleResponseResult();
        log.error("请求被拦截，拦截类型为：[{}], message：[{}]", flowException.getClass().getSimpleName(), flowException.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.REQ_LIMIT, flowException.getMessage());
    }

    @ExceptionHandler(value = AuthorityException.class)
    public ApiResult<?> blockExceptionHandler(AuthorityException authorityException) {
        handleResponseResult();
        log.error("请求被拦截，拦截类型为：[{}], message：[{}]", authorityException.getClass().getSimpleName(), authorityException.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.REQ_BLACKLIST, authorityException.getMessage());
    }

    @ExceptionHandler(value = SystemBlockException.class)
    public ApiResult<?> blockExceptionHandler(SystemBlockException systemBlockException) {
        handleResponseResult();
        log.error("请求被拦截，拦截类型为：[{}], message：[{}]", systemBlockException.getClass().getSimpleName(), systemBlockException.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.SYSTEM_BLOCK, systemBlockException.getMessage());
    }

    @ExceptionHandler(value = ParamFlowException.class)
    public ApiResult<?> blockExceptionHandler(ParamFlowException paramFlowException) {
        handleResponseResult();
        log.error("请求被拦截，拦截类型为：[{}], message：[{}]", paramFlowException.getClass().getSimpleName(), paramFlowException.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.PARAM_FLOW, paramFlowException.getMessage());
    }

    @ExceptionHandler(value = DegradeException.class)
    public ApiResult<?> blockExceptionHandler(DegradeException degradeException) {
        log.error("请求被拦截，拦截类型为：[{}], message：[{}]", degradeException.getClass().getSimpleName(), degradeException.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.SERVICE_DEGRADATION);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 13;
    }
}
