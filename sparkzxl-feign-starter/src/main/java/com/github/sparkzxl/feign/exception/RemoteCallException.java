package com.github.sparkzxl.feign.exception;

import com.github.sparkzxl.entity.response.ResponseCode;
import feign.FeignException;
import feign.Request;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * description: feign远程调用异常
 *
 * @author zhouxinlei
 */
@Slf4j
@Getter
@Setter
@ToString
public class RemoteCallException extends FeignException {

    private String errorCode;

    private String errorMessage;

    public RemoteCallException(int status, String errorCode, String errorMessage, Request request) {
        super(status, errorMessage, request);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public RemoteCallException(int status, String errorCode, String errorMessage, Throwable cause, Request request) {
        super(status, errorMessage, request, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
