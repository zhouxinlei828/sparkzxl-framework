package com.github.sparkzxl.gateway.response.strategy;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.entity.response.Response;
import com.github.sparkzxl.gateway.response.ExceptionHandlerResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;

/**
 * description: 默认异常处理策略
 *
 * @author zhoux
 */
@Slf4j
public class DefaultExceptionHandlerStrategy implements ExceptionHandlerStrategy {

    @Override
    public Class getHandleClass() {
        return Throwable.class;
    }

    @Override
    public ExceptionHandlerResult handleException(Throwable throwable) {
        Response response = Response.failDetail(ExceptionErrorCode.FAILURE.getCode(), throwable.getMessage());
        ExceptionHandlerResult result = new ExceptionHandlerResult(HttpStatus.INTERNAL_SERVER_ERROR, JSON.toJSONString(response));
        if (log.isDebugEnabled()) {
            log.debug("Handle Throwable:{}", ExceptionUtils.getStackTrace(throwable));
        }
        log.error("Handle Throwable Message:{}", ExceptionUtils.getRootCause(throwable));
        return result;
    }
}
