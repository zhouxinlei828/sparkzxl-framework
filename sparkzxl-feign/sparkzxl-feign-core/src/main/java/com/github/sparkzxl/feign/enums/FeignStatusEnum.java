package com.github.sparkzxl.feign.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description: feign自定义状态枚举类
 *
 * @author zhouxinlei
 * @since 2022-04-07 13:31:48
 */
@Getter
@AllArgsConstructor
public enum FeignStatusEnum {
    /**
     * 传递异常
     */
    TRANSFER_EXCEPTION(520),
    /**
     * 未知异常
     */
    UNKNOWN_EXCEPTION(521),
    ;

    final int code;

}
