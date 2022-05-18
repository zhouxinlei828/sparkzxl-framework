package com.github.sparkzxl.alarm.entity.wechat;

/**
 * description: WeTalk支持的消息类型
 *
 * @author zhouxinlei
 * @since 2022-05-18 17:14:51
 */
public enum WeTalkMsgType {
    /**
     * text类型
     */
    TEXT("text"),

    /**
     * markdown类型
     */
    MARKDOWN("markdown"),

    /**
     * 图片类型
     */
    IMAGE("image"),

    /**
     * 图文类型
     */
    NEWS("news"),

    /**
     * 文件类型
     */
    FILE("file");

    private String type;

    WeTalkMsgType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}