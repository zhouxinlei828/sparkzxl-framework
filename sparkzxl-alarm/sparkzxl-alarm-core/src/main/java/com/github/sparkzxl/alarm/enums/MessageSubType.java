package com.github.sparkzxl.alarm.enums;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.Message;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.entity.dingtalk.*;
import com.github.sparkzxl.alarm.entity.wechat.WeMarkdown;
import com.github.sparkzxl.alarm.entity.wechat.WeNews;
import com.github.sparkzxl.alarm.entity.wechat.WeText;
import com.github.sparkzxl.alarm.exception.AlarmException;

import java.util.*;

import static com.github.sparkzxl.alarm.constant.AlarmConstant.WETALK_AT_ALL;

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
    TEXT(true) {
        @Override
        public MsgType msgType(AlarmType alarmType, AlarmRequest request) {
            String content = request.getContent();
            boolean atAll = request.isAtAll();
            List<String> phones = request.getPhones();
            if (alarmType == AlarmType.DINGTALK) {
                Message message = new DingTalkText(new DingTalkText.Text(content));
                if (atAll) {
                    message.setAt(new Message.At(true));
                } else if (phones != null && !phones.isEmpty()) {
                    message.setAt(new Message.At(phones));
                }
                return message;
            } else {
                WeText.Text text = new WeText.Text(content);
                WeText weText = new WeText(text);
                if (atAll) {
                    text.setMentioned_mobile_list(Collections.singletonList(WETALK_AT_ALL));
                } else if (phones != null && !phones.isEmpty()) {
                    text.setMentioned_mobile_list(phones);
                }
                return weText;
            }
        }
    },

    /**
     * Markdown类型
     */
    MARKDOWN(true) {
        @Override
        public MsgType msgType(AlarmType alarmType, AlarmRequest request) {
            String content = request.getContent();
            String title = request.getTitle();
            List<String> phones = request.getPhones();
            if (alarmType == AlarmType.DINGTALK) {
                Message message = new DingTalkMarkDown(new DingTalkMarkDown.MarkDown(title, content));
                if (!phones.isEmpty()) {
                    message.setAt(new Message.At(phones));
                }
                return message;
            } else {
                WeMarkdown.Markdown markdown = new WeMarkdown.Markdown(content);
                return new WeMarkdown(markdown);
            }
        }
    },

    /**
     * 图文类型
     */
    IMAGE_TEXT(true) {
        @Override
        public MsgType msgType(AlarmType alarmType, AlarmRequest request) {
            if (alarmType == AlarmType.DINGTALK) {
                return new DingFeedCard(new ArrayList<>());
            } else {
                return new WeNews(new ArrayList<>());
            }
        }
    },
    /**
     * link类型, 只支持 {@link AlarmType#DINGTALK}
     */
    LINK(true) {
        @Override
        public MsgType msgType(AlarmType alarmType, AlarmRequest request) {
            if (alarmType == AlarmType.DINGTALK) {
                return new DingTalkLink();
            } else {
                throw new AlarmException(AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.code(), AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.message());
            }
        }
    },
    /**
     * actionCard类型, 只支持 {@link AlarmType#DINGTALK}
     */
    ACTION_CARD(true) {
        @Override
        public MsgType msgType(AlarmType alarmType, AlarmRequest request) {
            if (alarmType == AlarmType.DINGTALK) {
                return new DingTalkActionCard();
            } else {
                throw new AlarmException(AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.code(), AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.message());
            }
        }
    };

    public abstract MsgType msgType(AlarmType alarmType, AlarmRequest request);

    /**
     * 是否支持显示设置消息子类型调用
     */
    private final boolean support;

    MessageSubType(boolean support) {
        this.support = support;
    }

    public boolean isSupport() {
        return support;
    }

    public static boolean contains(String value) {
        return Arrays.stream(MessageSubType.values()).anyMatch(e -> Objects.equals(e.name(), value));
    }
}
