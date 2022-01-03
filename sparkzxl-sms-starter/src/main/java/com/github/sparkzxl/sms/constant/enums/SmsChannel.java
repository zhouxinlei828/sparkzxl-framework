package com.github.sparkzxl.sms.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * description: 短信渠道枚举
 *
 * @author zhouxinlei
 * @date 2021-12-28 10:18:10
 */
@Getter
@RequiredArgsConstructor
public enum SmsChannel {
    ALIYUN("aliyun"),
    TENCENT("tencent");
    private final String value;

}
