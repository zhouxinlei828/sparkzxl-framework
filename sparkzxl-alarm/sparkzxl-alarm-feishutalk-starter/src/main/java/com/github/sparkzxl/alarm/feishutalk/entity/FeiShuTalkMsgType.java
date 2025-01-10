package com.github.sparkzxl.alarm.feishutalk.entity;

/**
 * 飞书支持的消息类型
 *
 * @author zhouxinlei
 * @since 1.0
 */
public enum FeiShuTalkMsgType {
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
    ACTION_CARD("interactive"),

    /**
     * FeedCard类型
     */
    FEED_CARD("feedCard");

    private final String type;

    FeiShuTalkMsgType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
