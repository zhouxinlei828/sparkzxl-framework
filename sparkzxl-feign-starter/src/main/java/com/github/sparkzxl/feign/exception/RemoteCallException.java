package com.github.sparkzxl.feign.exception;

import com.github.sparkzxl.model.exception.ExceptionChain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * description: feign远程调用异常
 *
 * @author zhouxinlei
 */
@Slf4j
@Getter
@Setter
@ToString
public class RemoteCallException extends RuntimeException {

    private String errorCode;

    private String applicationName;

    public RemoteCallException(String errorCode, String errorMessage, String applicationName) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.applicationName = applicationName;
    }

    public RemoteCallException(String errorCode, String errorMessage, String applicationName, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.applicationName = applicationName;
    }
}
