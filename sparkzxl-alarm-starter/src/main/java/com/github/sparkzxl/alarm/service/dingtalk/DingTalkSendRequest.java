package com.github.sparkzxl.alarm.service.dingtalk;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author weilai
 */
@Data
public class DingTalkSendRequest {

    private String msgtype;

    private Text text;

    private Markdown markdown;

    @Data
    @AllArgsConstructor
    public static class Text {

        private String content;
    }

    @Data
    @AllArgsConstructor
    public static class Markdown {

        private String title;
        private String text;

    }
}
