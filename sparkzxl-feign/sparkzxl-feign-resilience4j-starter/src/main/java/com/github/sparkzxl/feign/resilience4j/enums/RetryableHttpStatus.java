package com.github.sparkzxl.feign.resilience4j.enums;

/**
 * description: Retryable http status
 *
 * @author zhouxinlei
 * @since 2022-04-04 11:06:58
 */
public enum RetryableHttpStatus {

    /**
     * 断路器打开
     */
    CIRCUIT_BREAKER_ON(581),
    /**
     * 可以重试的异常
     */
    RETRYABLE_IO_EXCEPTION(582),
    /**
     * 不能重试的异常
     */
    NOT_RETRYABLE_IO_EXCEPTION(583),
    /**
     * 超过限流限制
     */
    BULKHEAD_FULL(584),
    ;
    private final int value;

    RetryableHttpStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
