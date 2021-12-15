package com.github.sparkzxl.gateway.response.strategy;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
import com.github.sparkzxl.gateway.response.ExceptionHandlerResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;

/**
 * description: 服务器响应异常处理
 *
 * @author zhoux
 */
@Slf4j
public class ResponseStatusExceptionHandlerStrategy implements ExceptionHandlerStrategy {

    @Override
    public Class getHandleClass() {
        return ResponseStatusException.class;
    }

    @Override
    public ExceptionHandlerResult handleException(Throwable throwable) {
        ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
        ResponseResult responseResult = ResponseResult.result(ResponseInfoStatus.OPEN_SERVICE_UNAVAILABLE.getCode(), throwable.getMessage());
        String response = JSON.toJSONString(responseResult);
        ExceptionHandlerResult result = new ExceptionHandlerResult(responseStatusException.getStatus(), response);
        log.debug("[ResponseStatusExceptionHandlerStrategy]Handle Exception:{},Result:{}", throwable.getMessage(), result);
        return result;
    }
}
