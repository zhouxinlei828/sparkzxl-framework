package com.github.sparkzxl.service.dingtalk;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author weilai
 */
@Data
public class DingTalkSendRequest {

    private String msgtype;

    private Text text;

    @Data
    @AllArgsConstructor
    public static class Text {

        private String content;
    }
}
