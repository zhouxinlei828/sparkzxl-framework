package com.github.sparkzxl.distributed.dubbo.support;


import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.support.ResponseResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * description: 全局异常处理
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:44:48
 */
@ControllerAdvice
@RestController
@Slf4j
public class SentinelExceptionHandler {

    @ExceptionHandler(value = FlowException.class)
    public ApiResult blockExceptionHandler(FlowException flowException) {

        log.error("请求被拦截，拦截类型为：{}, message：{}",flowException.getClass().getSimpleName(),flowException.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.REQ_LIMIT, flowException.getMessage());
    }

    @ExceptionHandler(value = AuthorityException.class)
    public ApiResult blockExceptionHandler(AuthorityException authorityException) {
        log.error("请求被拦截，拦截类型为：{}, message：{}",authorityException.getClass().getSimpleName(),authorityException.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.REQ_BLACKLIST, authorityException.getMessage());
    }

    @ExceptionHandler(value = SystemBlockException.class)
    public ApiResult blockExceptionHandler(SystemBlockException systemBlockException) {
        log.error("请求被拦截，拦截类型为：{}, message：{}",systemBlockException.getClass().getSimpleName(),systemBlockException.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.SYSTEM_BLOCK, systemBlockException.getMessage());
    }

    @ExceptionHandler(value = ParamFlowException.class)
    public ApiResult blockExceptionHandler(ParamFlowException paramFlowException) {
        log.error("请求被拦截，拦截类型为：{}, message：{}",paramFlowException.getClass().getSimpleName(),paramFlowException.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.PARAM_FLOW, paramFlowException.getMessage());
    }

    @ExceptionHandler(value = DegradeException.class)
    public ApiResult blockExceptionHandler(DegradeException degradeException) {
        log.error("请求被拦截，拦截类型为：{}, message：{}",degradeException.getClass().getSimpleName(),degradeException.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.SERVICE_DEGRADATION);
    }
}
