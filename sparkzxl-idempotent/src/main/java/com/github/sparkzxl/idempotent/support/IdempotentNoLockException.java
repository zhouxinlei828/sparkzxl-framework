package com.github.sparkzxl.idempotent.support;

/**
 * description: 幂等无法获取锁异常
 *
 * @author zhouxinlei
 */
public class IdempotentNoLockException extends RuntimeException {

    public IdempotentNoLockException() {
    }

    public IdempotentNoLockException(String message) {
        super(message);
    }

    public IdempotentNoLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdempotentNoLockException(Throwable cause) {
        super(cause);
    }

    public IdempotentNoLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
