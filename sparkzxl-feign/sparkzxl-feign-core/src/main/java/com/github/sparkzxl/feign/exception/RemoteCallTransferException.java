package com.github.sparkzxl.feign.exception;

import feign.FeignException;
import feign.Request;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * description: feign远程调用传递异常
 *
 * @author zhouxinlei
 */
@Slf4j
@Getter
@Setter
@ToString
public class RemoteCallTransferException extends FeignException {

    private String errorCode;

    private String errorMsg;

    private Object object;

    public RemoteCallTransferException(int status, String errorCode, String errorMsg, Object object, Request request) {
        super(status, errorMsg, request);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.object = object;
    }

    public RemoteCallTransferException(int status, String errorCode, String errorMsg, Throwable cause, Object object, Request request) {
        super(status, errorMsg, request, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.object = object;
    }
}
