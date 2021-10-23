package com.github.sparkzxl.gateway.response.strategy;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
import com.github.sparkzxl.gateway.response.ExceptionHandlerResult;
import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * description: 服务器连接超时异常处理
 *
 * @author zhoux
 * @date 2021-10-23 16:34:14
 */
@Slf4j
public class ConnectTimeoutExceptionHandlerStrategy implements ExceptionHandlerStrategy {

    @Override
    public Class getHandleClass() {
        return ConnectTimeoutException.class;
    }

    @Override
    public ExceptionHandlerResult handleException(Throwable throwable) {
        ResponseResult responseResult = ResponseResult.result(ResponseInfoStatus.FAILURE.getCode(), throwable.getMessage());
        String response = JSON.toJSONString(responseResult);
        ExceptionHandlerResult result = new ExceptionHandlerResult(HttpStatus.INTERNAL_SERVER_ERROR, response);
        log.debug("Handle ConnectTimeoutException:{},Result:{}", throwable.getMessage(), result);
        return result;
    }
}
