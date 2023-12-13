package com.github.sparkzxl.gateway.plugin.exception.strategy;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.base.result.R;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import com.github.sparkzxl.gateway.plugin.exception.result.ExceptionHandlerResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * description: 默认异常处理策略
 *
 * @author zhoux
 */
@Slf4j
public class DefaultExceptionHandlerStrategy implements ExceptionHandlerStrategy<Throwable> {

    @Override
    public Class<Throwable> getHandleClass() {
        return Throwable.class;
    }

    @Override
    public ExceptionHandlerResult handleException(Throwable throwable) {
        log.error("Throwable：", throwable);
        R r = R.failDetail(ResultErrorCode.FAILURE.getErrorCode(), throwable.getMessage());
        return new ExceptionHandlerResult(HttpStatus.INTERNAL_SERVER_ERROR, JSON.toJSONString(r));
    }
}
