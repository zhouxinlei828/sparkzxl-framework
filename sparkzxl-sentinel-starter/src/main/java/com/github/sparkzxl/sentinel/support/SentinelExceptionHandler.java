package com.github.sparkzxl.sentinel.support;


import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.github.sparkzxl.core.base.result.R;
import com.github.sparkzxl.core.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
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
@RestController
@Slf4j
public class SentinelExceptionHandler implements Ordered {

    @ExceptionHandler(value = FlowException.class)
    public R<?> handleFlowException(FlowException e) {
        log.error("FlowException 异常:", e);
        return R.failDetail(ResultErrorCode.REQ_LIMIT.getErrorCode(), ResultErrorCode.REQ_LIMIT.getErrorMsg());
    }

    @ExceptionHandler(value = AuthorityException.class)
    public R<?> handleAuthorityException(AuthorityException e) {
        log.error("AuthorityException 异常:", e);
        return R.failDetail(
                ResultErrorCode.REQ_BLACKLIST.getErrorCode(), ResultErrorCode.REQ_BLACKLIST.getErrorMsg());
    }

    @ExceptionHandler(value = SystemBlockException.class)
    public R<?> handleSystemBlockException(SystemBlockException e) {
        log.error("SystemBlockException 异常:", e);
        return R.failDetail(
                ResultErrorCode.SYSTEM_BLOCK.getErrorCode(), ResultErrorCode.SYSTEM_BLOCK.getErrorMsg());
    }

    @ExceptionHandler(value = ParamFlowException.class)
    public R<?> handleParamFlowException(ParamFlowException e) {
        log.error("ParamFlowException 异常:", e);
        return R.failDetail(
                ResultErrorCode.PARAM_FLOW.getErrorCode(), ResultErrorCode.PARAM_FLOW.getErrorMsg());
    }

    @ExceptionHandler(value = DegradeException.class)
    public R<?> handleDegradeException(DegradeException e) {
        log.error("DegradeException 异常:", e);
        return R.failDetail(
                ResultErrorCode.SERVICE_DEGRADATION.getErrorCode(), ResultErrorCode.SERVICE_DEGRADATION.getErrorMsg());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.SENTINEL_EXCEPTION_ORDER.getOrder();
    }
}
