package com.github.sparkzxl.sentinel.support;


import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.annotation.response.Response;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
import com.github.sparkzxl.core.util.RequestContextHolderUtils;
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
        Response response = (Response) servletRequest.getAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        boolean result = response != null;
        if (result) {
            servletRequest.removeAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        }
    }

    @ExceptionHandler(value = FlowException.class)
    public ResponseResult<?> blockExceptionHandler(FlowException e) {
        handleResponseResult();
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.REQ_LIMIT, e.getMessage());
    }

    @ExceptionHandler(value = AuthorityException.class)
    public ResponseResult<?> blockExceptionHandler(AuthorityException e) {
        handleResponseResult();
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.REQ_BLACKLIST, e.getMessage());
    }

    @ExceptionHandler(value = SystemBlockException.class)
    public ResponseResult<?> blockExceptionHandler(SystemBlockException e) {
        handleResponseResult();
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.SYSTEM_BLOCK, e.getMessage());
    }

    @ExceptionHandler(value = ParamFlowException.class)
    public ResponseResult<?> blockExceptionHandler(ParamFlowException e) {
        handleResponseResult();
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.PARAM_FLOW, e.getMessage());
    }

    @ExceptionHandler(value = DegradeException.class)
    public ResponseResult<?> blockExceptionHandler(DegradeException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.SERVICE_DEGRADATION);
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.SENTINEL_EXCEPTION_ORDER.getOrder();
    }
}
