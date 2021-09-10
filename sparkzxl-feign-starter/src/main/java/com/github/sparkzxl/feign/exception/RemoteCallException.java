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

    private Integer code;

    private String applicationName;

    public RemoteCallException(int code, String message, String applicationName) {
        super(message);
        this.code = code;
        this.applicationName = applicationName;
    }

    public RemoteCallException(int code, String message, String applicationName, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.applicationName = applicationName;
    }

    public RemoteCallException(int code, String message, String applicationName, List<ExceptionChain> exceptionChains) {
        super(message);
        this.code = code;
        this.applicationName = applicationName;
    }
}
