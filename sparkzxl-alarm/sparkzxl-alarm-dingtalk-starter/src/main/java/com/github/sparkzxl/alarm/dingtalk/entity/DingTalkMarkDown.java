package com.github.sparkzxl.alarm.dingtalk.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * description: 钉钉 Markdown 消息格式实体
 *
 * @author zhouxinlei
 * @since 2022-05-19 10:15:55
 */
public class DingTalkMarkDown extends Message {

    /**
     * {@link MarkDown}
     */
    private MarkDown markdown;

    public DingTalkMarkDown(MarkDown markdown) {
        setMsgtype(DingTalkMsgType.MARKDOWN.type());
        this.markdown = markdown;
    }

    public MarkDown getMarkdown() {
        return markdown;
    }

    public void setMarkdown(MarkDown markdown) {
        this.markdown = markdown;
    }

    public static class MarkDown implements Serializable {

        /**
         * 首屏会话透出的展示内容, 不会展示在具体的显示内容上
         */
        private String title;
        /**
         * markdown格式的消息
         */
        private String text;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public MarkDown() {
        }

        public MarkDown(String title, String text) {
            this.title = title;
            this.text = text;
        }
    }

    @Override
    public void transfer(Map<String, Object> params) {
        this.markdown.text = replaceContent(this.markdown.text, params);
    }
}
