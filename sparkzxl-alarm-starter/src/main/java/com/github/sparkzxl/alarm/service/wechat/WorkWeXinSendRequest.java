package com.github.sparkzxl.alarm.service.wechat;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author weilai
 */
@Data
public class WorkWeXinSendRequest {

    private String touser;

    private Integer agentid;

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

        private String content;

    }

}
