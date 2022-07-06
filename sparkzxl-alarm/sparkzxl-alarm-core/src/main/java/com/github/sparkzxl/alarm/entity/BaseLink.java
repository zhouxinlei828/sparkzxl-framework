package com.github.sparkzxl.alarm.entity;

/**
 * description: Link类型实体对象
 *
 * @author zhouxinlei
 * @since 2022-05-19 10:38:05
 */
public class BaseLink {
    /**
     * 消息标题
     */
    private String title;
    /**
     * 消息内容。如果太长只会部分展示
     */
    private String text;
    /**
     * 点击消息跳转的URL
     */
    private String messageUrl;
    /**
     * 图片URL
     */
    private String picUrl;

    private BaseLink(String title, String text, String messageUrl, String picUrl) {
        this.title = title;
        this.text = text;
        this.messageUrl = messageUrl;
        this.picUrl = picUrl;
    }

    public static BaseLink instance(String title, String text, String messageUrl, String picUrl) {
        return new BaseLink(title, text, messageUrl, picUrl);
    }

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

    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}