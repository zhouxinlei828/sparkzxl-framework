package com.github.sparkzxl.alarm.enums;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.DingTalkText;
import com.github.sparkzxl.alarm.entity.Message;
import com.github.sparkzxl.alarm.entity.MsgType;

import java.util.Arrays;
import java.util.List;
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
    TEXT(true) {
        @Override
        public MsgType msgType(AlarmType alarmType, AlarmRequest request) {
            String content = request.getContent();
            boolean atAll = request.isAtAll();
            List<String> phones = request.getPhones();
            if (alarmType == AlarmType.DING_TALK) {
                Message message = new DingTalkText(new DingTalkText.Text(content));
                if (atAll) {
                    message.setAt(new Message.At(true));
                } else if (phones != null && !phones.isEmpty()) {
                    message.setAt(new Message.At(phones));
                }
                return message;
            } else {
                return null;
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
        return Arrays.stream(MessageSubType.values()).filter(e -> Objects.equals(e.name(), value)).count() > 0;
    }
}
