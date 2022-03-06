package com.github.sparkzxl.sentinel.support;


import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.entity.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

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

    @ExceptionHandler(value = FlowException.class)
    public Response<?> handleFlowException(FlowException e) {
        log.error("FlowException异常:", e);
        return Response.failDetail(ExceptionErrorCode.REQ_LIMIT.getErrorCode(), ExceptionErrorCode.REQ_LIMIT.getErrorMessage());
    }

    @ExceptionHandler(value = AuthorityException.class)
    public Response<?> handleAuthorityException(AuthorityException e) {
        log.error("AuthorityException异常:", e);
        return Response.failDetail(
                ExceptionErrorCode.REQ_BLACKLIST.getErrorCode(), ExceptionErrorCode.REQ_BLACKLIST.getErrorMessage());
    }

    @ExceptionHandler(value = SystemBlockException.class)
    public Response<?> handleSystemBlockException(SystemBlockException e) {
        log.error("SystemBlockException异常:", e);
        return Response.failDetail(
                ExceptionErrorCode.SYSTEM_BLOCK.getErrorCode(), ExceptionErrorCode.SYSTEM_BLOCK.getErrorMessage());
    }

    @ExceptionHandler(value = ParamFlowException.class)
    public Response<?> handleParamFlowException(ParamFlowException e) {
        log.error("ParamFlowException异常:", e);
        return Response.failDetail(
                ExceptionErrorCode.PARAM_FLOW.getErrorCode(), ExceptionErrorCode.PARAM_FLOW.getErrorMessage());
    }

    @ExceptionHandler(value = DegradeException.class)
    public Response<?> handleDegradeException(DegradeException e) {
        log.error("DegradeException异常:", e);
        return Response.failDetail(
                ExceptionErrorCode.SERVICE_DEGRADATION.getErrorCode(), ExceptionErrorCode.SERVICE_DEGRADATION.getErrorMessage());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.SENTINEL_EXCEPTION_ORDER.getOrder();
    }
}
