package com.github.sparkzxl.client.support;

/**
 * description: 限流降级异常
 *
 * @author zhouxinlei
 * @since 2024-07-01 15:19:55
 */
public class LimitFallbackException extends RuntimeException {

    public LimitFallbackException(String message, Throwable cause) {
        super(message, cause);
    }

}
