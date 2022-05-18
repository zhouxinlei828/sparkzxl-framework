package com.github.sparkzxl.alarm.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * description: 钉钉文本消息
 *
 * @author zhouxinlei
 * @since 2022-05-18 13:51:30
 */
public class DingTalkText extends Message {

    /**
     * 消息内容
     */
    private Text text;

    public DingTalkText(Text text) {
        this.text = text;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public static class Text implements Serializable {
        private String content;

        public Text() {
        }

        public Text(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }

    @Override
    public void transfer(Map<String, Object> params) {
        this.text.content = replaceContent(this.text.content, params);
    }

}
