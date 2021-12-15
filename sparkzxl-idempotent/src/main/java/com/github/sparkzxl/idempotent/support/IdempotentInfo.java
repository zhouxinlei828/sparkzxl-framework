package com.github.sparkzxl.idempotent.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description: 幂等信息类
 *
 * @author zhouxinlei
 */
@Getter
@AllArgsConstructor
public class IdempotentInfo {

    private String key;

    private Long maxLockMilli;

    private String message;

}
