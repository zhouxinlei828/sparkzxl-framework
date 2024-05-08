package com.github.sparkzxl.gateway.plugin.exception.strategy;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.base.result.R;
import com.github.sparkzxl.core.support.code.ExceptionErrorCode;
import com.github.sparkzxl.gateway.plugin.exception.result.ExceptionHandlerResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        log.error("ResponseStatusException：", throwable);
        ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
        HttpStatus exceptionStatus = responseStatusException.getStatus();
        R result;
        switch (exceptionStatus) {
            case NOT_FOUND:
                result = R.failDetail(ExceptionErrorCode.NOT_FOUND.getErrorCode(), ExceptionErrorCode.NOT_FOUND.getErrorMsg());
                break;
            case SERVICE_UNAVAILABLE:
                result = R.failDetail(ExceptionErrorCode.OPEN_SERVICE_UNAVAILABLE.getErrorCode(), "服务不可用");
                break;
            case GATEWAY_TIMEOUT:
                result = R.failDetail(ExceptionErrorCode.TIME_OUT_ERROR.getErrorCode(), ExceptionErrorCode.TIME_OUT_ERROR.getErrorMsg());
                break;
            default:
                result = R.failDetail(ExceptionErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
                        ExceptionErrorCode.INTERNAL_SERVER_ERROR.getErrorMsg());
                break;
        }
        String response = JSON.toJSONString(result);
        return new ExceptionHandlerResult(exceptionStatus, response);
    }
}
