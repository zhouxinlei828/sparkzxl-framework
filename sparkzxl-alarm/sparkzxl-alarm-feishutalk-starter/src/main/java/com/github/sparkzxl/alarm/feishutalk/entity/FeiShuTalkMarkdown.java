package com.github.sparkzxl.alarm.feishutalk.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.sparkzxl.core.util.StrPool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * description: 飞书markdown消息
 *
 * @author zhouxinlei
 * @since 2022-05-18 13:51:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeiShuTalkMarkdown extends Message {

    @JsonProperty("card")
    private Card card;

    public FeiShuTalkMarkdown() {
        setMsgtype(FeiShuTalkMsgType.ACTION_CARD.type());
    }

    @Override
    public void transfer(Map<String, Object> params) {
        String content = this.card.elements.get(0).content;
        this.card.elements.get(0).content = replaceContent(content, params);
    }

    @Override
    public String toJson() {
        List<FeiShuAt> atList = super.getAtList();
        StringBuilder atListStr = new StringBuilder();
        if (CollectionUtils.isNotEmpty(atList)) {
            atList.forEach(at -> {
                String toXML = at.toXML();
                atListStr.append(toXML).append(StrPool.NEWLINE);
            });
        }
        Card card = getCard();
        List<Element> elementList = card.getElements();
        Element element = elementList.get(0);
        String text = element.getContent();
        if (StringUtils.isNotEmpty(text)) {
            element.setTag("markdown");
            element.setContent(text.concat(StrPool.NEWLINE).concat(atListStr.toString()));
        }
        return super.toJson();
    }

    @NoArgsConstructor
    @Data
    public static class Card {

        @JsonProperty("config")
        private Config config;

        @JsonProperty("header")
        private Header header;
        @JsonProperty("elements")
        private List<Element> elements;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Config {

        /**
         * 宽屏模式
         */
        @JsonProperty("wide_screen_mode")
        private Boolean wideScreenMode;
    }

    @NoArgsConstructor
    @Data
    public static class Header {

        /**
         * 配置卡片标题内容
         */
        @JsonProperty("title")
        private Title title;

        /**
         * 控制标题背景颜色，取值参考注意事项
         */
        @JsonProperty("template")
        private String template = "blue";

        /**
         * 配置卡片标题内容
         */
        @NoArgsConstructor
        @AllArgsConstructor
        @Data
        public static class Title {
            @JsonProperty("tag")
            private String tag;

            /**
             * 卡片标题文案内容
             */
            @JsonProperty("content")
            private String content;
        }
    }

    @NoArgsConstructor
    @Data
    public static class Element {

        @JsonProperty("tag")
        private String tag;

        @JsonProperty("content")
        private String content;

        @JsonProperty("href")
        private Href href;

        @NoArgsConstructor
        @Data
        public static class Href {

            @JsonProperty("urlVal")
            private UrlVal urlVal;

            @NoArgsConstructor
            @Data
            public static class UrlVal {

                @JsonProperty("url")
                private String url;

                @JsonProperty("android_url")
                private String androidUrl;

                @JsonProperty("ios_url")
                private String iosUrl;

                @JsonProperty("pc_url")
                private String pcUrl;

            }
        }
    }

}
