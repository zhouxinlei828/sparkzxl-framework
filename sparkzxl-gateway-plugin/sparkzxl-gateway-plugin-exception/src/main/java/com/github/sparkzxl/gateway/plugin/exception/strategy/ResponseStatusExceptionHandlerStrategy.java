package com.github.sparkzxl.gateway.plugin.exception.strategy;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import com.github.sparkzxl.core.base.result.Response;
import com.github.sparkzxl.gateway.plugin.exception.result.ExceptionHandlerResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;

/**
 * description: 服务器响应异常处理
 *
 * @author zhoux
 */
@Slf4j
public class ResponseStatusExceptionHandlerStrategy implements ExceptionHandlerStrategy<ResponseStatusException> {

    @Override
    public Class<ResponseStatusException> getHandleClass() {
        return ResponseStatusException.class;
    }

    @Override
    public ExceptionHandlerResult handleException(Throwable throwable) {
        ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
        Response<?> responseResult = Response.fail(ResultErrorCode.OPEN_SERVICE_UNAVAILABLE.getErrorCode(), throwable.getMessage());
        String response = JSON.toJSONString(responseResult);
        ExceptionHandlerResult result = new ExceptionHandlerResult(responseStatusException.getStatus(), response);
        if (log.isDebugEnabled()) {
            log.debug("Handle Exception:{},Result:{}", throwable.getMessage(), result);
        }
        return result;
    }
}
