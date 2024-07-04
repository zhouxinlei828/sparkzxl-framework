package com.github.sparkzxl.client.support;

/**
 * description: 断路器没有降级异常
 *
 * @author zhouxinlei
 * @since 2024-07-01 15:19:55
 */
public class NoFallbackAvailableException extends RuntimeException {

    public NoFallbackAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

}
