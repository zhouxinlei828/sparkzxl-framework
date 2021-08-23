package com.github.sparkzxl.service.dingtalk;

import lombok.Getter;

/**
 * @author weilai
 */
@Getter
public enum DingTalkSendMsgTypeEnum {

    /**
     * text
     */
    TEXT("text"),
    ;

    private String type;

    DingTalkSendMsgTypeEnum(String type) {
        this.type = type;
    }
}
