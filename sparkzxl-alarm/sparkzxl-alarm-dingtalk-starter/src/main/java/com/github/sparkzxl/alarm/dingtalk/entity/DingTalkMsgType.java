package com.github.sparkzxl.alarm.dingtalk.entity;

/**
 * DingTalk支持的消息类型
 *
 * @author zhouxinlei
 * @since 1.0
 */
public enum DingTalkMsgType {
    /**
     * text类型
     */
    TEXT("text"),

    /**
     * link类型
     */
    LINK("link"),

    /**
     * markdown类型
     */
    MARKDOWN("markdown"),

    /**
     * ActionCard类型
     */
    ACTION_CARD("actionCard"),

    /**
     * FeedCard类型
     */
    FEED_CARD("feedCard");

    private final String type;

    DingTalkMsgType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
