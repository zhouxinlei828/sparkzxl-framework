package com.github.sparkzxl.feign.exception;

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
public class RemoteCallTransferException extends FeignException {

    private String errorCode;

    private String errorMessage;

    private Object object;

    public RemoteCallTransferException(int status, String errorCode, String errorMessage, Object object, Request request) {
        super(status, errorMessage, request);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.object = object;
    }

    public RemoteCallTransferException(int status, String errorCode, String errorMessage, Throwable cause, Object object, Request request) {
        super(status, errorMessage, request, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.object = object;
    }
}
