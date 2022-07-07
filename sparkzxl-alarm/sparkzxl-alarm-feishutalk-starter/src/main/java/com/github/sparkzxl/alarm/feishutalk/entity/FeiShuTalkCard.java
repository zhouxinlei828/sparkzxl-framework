package com.github.sparkzxl.alarm.feishutalk.entity;


import cn.hutool.core.convert.Convert;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * description: 飞书活动卡片消息
 *
 * @author zhouxinlei
 * @since 2022-05-18 13:51:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeiShuTalkCard extends Message {

    @JsonProperty("card")
    private Card card;

    public FeiShuTalkCard() {
        setMsgtype(FeiShuTalkMsgType.ACTION_CARD.type());
    }

    @Override
    public void transfer(Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof Card) {
                this.card = Convert.convert(Card.class, entry.getValue());
                break;
            }
        }
    }

    @NoArgsConstructor
    @Data
    public static class Card {
        @JsonProperty("config")
        private Config config;
        @JsonProperty("elements")
        private List<Element> elements;
        @JsonProperty("header")
        private Header header;

        @NoArgsConstructor
        @Data
        public static class Config {
            @JsonProperty("wide_screen_mode")
            private Boolean wideScreenMode;
            @JsonProperty("enable_forward")
            private Boolean enableForward;
        }

        @NoArgsConstructor
        @Data
        public static class Header {
            @JsonProperty("title")
            private Title title;

            @NoArgsConstructor
            @Data
            public static class Title {
                @JsonProperty("content")
                private String content;
                @JsonProperty("tag")
                private String tag;
            }
        }

        @NoArgsConstructor
        @Data
        public static class Element {
            @JsonProperty("tag")
            private String tag;
            @JsonProperty("text")
            private Text text;
            @JsonProperty("actions")
            private List<Action> actions;

            @NoArgsConstructor
            @Data
            public static class Text {
                @JsonProperty("content")
                private String content;
                @JsonProperty("tag")
                private String tag;
            }

            @NoArgsConstructor
            @Data
            public static class Action {
                @JsonProperty("tag")
                private String tag;
                @JsonProperty("text")
                private Text text;
                @JsonProperty("url")
                private String url;
                @JsonProperty("type")
                private String type;
                @JsonProperty("value")
                private Value value;

                @NoArgsConstructor
                @Data
                public static class Value {

                }
            }
        }
    }
}
