package com.github.sparkzxl.sms.constant.enums;

import lombok.Getter;
import lombok.ToString;

/**
 * description: 短信状态信息
 *
 * @author zhouxinlei
 * @since 2022-05-26 11:11:47
 */
@Getter
@ToString
public enum SmsStatus {

    SEND_SUCCESS(1, "调用渠道接口发送成功"),
    RECEIVE_SUCCESS(2, "用户收到短信(收到渠道短信回执，状态成功)"),
    RECEIVE_FAIL(3, "用户收不到短信(收到渠道短信回执，状态失败)"),
    SEND_FAIL(4, "调用渠道接口发送失败");

    private final Integer code;
    private final String description;

    SmsStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据状态获取描述信息
     *
     * @param code code
     * @return String
     */
    public static String getDescription(Integer code) {
        for (SmsStatus value : SmsStatus.values()) {
            if (value.getCode().equals(code)) {
                return value.getDescription();
            }
        }
        return "";
    }


}
