package com.github.sparkzxl.gateway.plugin.exception.strategy;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.base.HttpCode;
import com.github.sparkzxl.core.base.result.R;
import com.github.sparkzxl.gateway.plugin.exception.result.ExceptionHandlerResult;
import com.github.sparkzxl.gateway.support.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * description: 认证异常异常处理
 *
 * @author zhoux
 */
@Slf4j
public class AuthenticationExceptionHandlerStrategy implements ExceptionHandlerStrategy<AuthenticationException> {

    @Override
    public Class<AuthenticationException> getHandleClass() {
        return AuthenticationException.class;
    }

    @Override
    public ExceptionHandlerResult handleException(Throwable throwable) {
        log.error("AuthenticationException异常信息：{}", throwable.getMessage());
        AuthenticationException e = (AuthenticationException) throwable;
        R result = R.fail(HttpCode.UNAUTHORIZED, e.getErrorCode(), e.getMessage());
        String response = JSON.toJSONString(result);
        return new ExceptionHandlerResult(HttpStatus.OK, response);
    }
}
