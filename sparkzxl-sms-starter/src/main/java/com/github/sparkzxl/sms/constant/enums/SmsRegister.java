package com.github.sparkzxl.sms.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * description: 短信注册商枚举
 *
 * @author zhouxinlei
 * @since 2021-12-28 10:18:10
 */
@Getter
@RequiredArgsConstructor
public enum SmsRegister {
    /**
     * 注册商
     */
    ALIYUN(1, "aliyun"),
    TENCENT(2, "tencent");
    private final Integer id;
    private final String name;

}
