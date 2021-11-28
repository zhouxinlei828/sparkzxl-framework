package com.github.sparkzxl.sentinel.support;


import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
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
    public ResponseResult<?> handleFlowException(FlowException e) {
        log.error("FlowException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.REQ_LIMIT, e.getMessage());
    }

    @ExceptionHandler(value = AuthorityException.class)
    public ResponseResult<?> handleAuthorityException(AuthorityException e) {
        log.error("AuthorityException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.REQ_BLACKLIST, e.getMessage());
    }

    @ExceptionHandler(value = SystemBlockException.class)
    public ResponseResult<?> handleSystemBlockException(SystemBlockException e) {
        log.error("SystemBlockException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.SYSTEM_BLOCK, e.getMessage());
    }

    @ExceptionHandler(value = ParamFlowException.class)
    public ResponseResult<?> handleParamFlowException(ParamFlowException e) {
        log.error("ParamFlowException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.PARAM_FLOW, e.getMessage());
    }

    @ExceptionHandler(value = DegradeException.class)
    public ResponseResult<?> handleDegradeException(DegradeException e) {
        log.error("DegradeException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.SERVICE_DEGRADATION);
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.SENTINEL_EXCEPTION_ORDER.getOrder();
    }
}
