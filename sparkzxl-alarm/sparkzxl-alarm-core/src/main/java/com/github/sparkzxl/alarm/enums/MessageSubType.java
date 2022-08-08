package com.github.sparkzxl.alarm.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * description: 消息体定义子类型
 *
 * @author zhouxinlei
 * @since 2022-05-18 09:53:48
 */
public enum MessageSubType {

    /**
     * 文本消息
     */
    TEXT(true, "text"),

    /**
     * Markdown类型
     */
    MARKDOWN(true, "markdown"),

    /**
     * 图文类型
     */
    IMAGE_TEXT(true, "image_text"),
    /**
     * link类型, 只支持 {@link AlarmChannel#DINGTALK}
     */
    LINK(true, "link"),
    /**
     * actionCard类型, 只支持 {@link AlarmChannel#DINGTALK}
     */
    ACTION_CARD(true, "action_card");

    /**
     * 是否支持显示设置消息子类型调用
     */
    private final boolean support;
    private final String code;

    MessageSubType(boolean support, String code) {
        this.support = support;
        this.code = code;
    }

    public boolean isSupport() {
        return support;
    }

    public String getCode() {
        return code;
    }

    public static boolean contains(String value) {
        return Arrays.stream(MessageSubType.values()).anyMatch(e -> Objects.equals(e.name(), value));
    }
}
