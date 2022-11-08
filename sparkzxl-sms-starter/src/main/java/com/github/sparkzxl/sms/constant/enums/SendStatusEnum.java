package com.github.sparkzxl.sms.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * description: 短信发送状态枚举
 *
 * @author zhouxinlei
 * @since 2021-12-28 10:18:10
 */
@Getter
@RequiredArgsConstructor
public enum SendStatusEnum {

    WAITING(1, "WAITING"),
    FAILURE(2, "FAIL"),
    SUCCESS(3, "SUCCESS");
    private final Integer code;
    private final String desc;

    public static SendStatusEnum get(Integer code) {
        for (SendStatusEnum sendStatusEnum : SendStatusEnum.values()) {
            if (sendStatusEnum.getCode().equals(code)) {
                return sendStatusEnum;
            }
        }
        return null;
    }

    public static SendStatusEnum get(String desc) {
        for (SendStatusEnum sendStatusEnum : SendStatusEnum.values()) {
            if (sendStatusEnum.getDesc() == desc) {
                return sendStatusEnum;
            }
        }
        return null;
    }


}
