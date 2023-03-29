package com.github.sparkzxl.lock.exception;

/**
 * description: 锁异常
 *
 * @author zhouxinlei
 * @since 2022-05-01 21:43:08
 */
public class LockException extends RuntimeException {

    public LockException() {
        super();
    }

    public LockException(String message) {

        super(message);
    }
}
