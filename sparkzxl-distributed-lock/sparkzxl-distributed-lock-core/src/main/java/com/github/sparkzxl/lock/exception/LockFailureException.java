package com.github.sparkzxl.lock.exception;

/**
 * description: 加锁失败异常
 *
 * @author zhouxinlei
 * @since 2022-05-01 21:43:45
 */
public class LockFailureException extends LockException {

    public LockFailureException() {

    }

    public LockFailureException(String message) {
        super(message);
    }
}
