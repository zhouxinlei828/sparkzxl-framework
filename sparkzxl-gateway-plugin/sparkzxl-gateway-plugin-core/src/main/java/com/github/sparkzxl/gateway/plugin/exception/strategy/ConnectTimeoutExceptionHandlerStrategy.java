package com.github.sparkzxl.gateway.plugin.exception.strategy;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import com.github.sparkzxl.gateway.plugin.exception.result.ExceptionHandlerResult;
import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * description: 服务器连接超时异常处理
 *
 * @author zhoux
 */
@Slf4j
public class ConnectTimeoutExceptionHandlerStrategy implements ExceptionHandlerStrategy<ConnectTimeoutException> {

    @Override
    public Class<ConnectTimeoutException> getHandleClass() {
        return ConnectTimeoutException.class;
    }

    @Override
    public ExceptionHandlerResult handleException(Throwable throwable) {
        ApiResult apiResult = ApiResult.fail(ResultErrorCode.FAILURE.getErrorCode(), throwable.getMessage());
        String response = JSON.toJSONString(apiResult);
        ExceptionHandlerResult result = new ExceptionHandlerResult(HttpStatus.REQUEST_TIMEOUT, response);
        log.debug("Handle ConnectTimeoutException:{},Result:{}", throwable.getMessage(), result);
        return result;
    }
}
