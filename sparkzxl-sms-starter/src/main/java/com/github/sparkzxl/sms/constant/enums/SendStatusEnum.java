package com.github.sparkzxl.sms.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * description: 短信发送状态枚举
 *
 * @author zhouxinlei
 * @date 2021-12-28 10:18:10
 */
@Getter
@RequiredArgsConstructor
public enum SendStatusEnum {

    WAITING(1L, "WAITING"),
    FAILURE(2L, "FAIL"),
    SUCCESS(3L, "SUCCESS");
    private final Long code;
    private final String desc;

    public static SendStatusEnum get(Long code) {
        for (SendStatusEnum sendStatusEnum : SendStatusEnum.values()) {
            if (sendStatusEnum.getCode() == code) {
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
