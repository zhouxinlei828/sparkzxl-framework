package com.github.sparkzxl.alarm.dingtalk.strategy;

import com.github.sparkzxl.alarm.dingtalk.entity.DingTalkMarkDown;
import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.dingtalk.entity.Message;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;
import com.github.sparkzxl.alarm.strategy.MessageSource;

import java.util.Set;

/**
 * description: 钉钉富文本消息
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:32:43
 */
public class MarkdownMsgHandleStrategy implements MsgHandleStrategy {

    @Override
    public MsgType newInstance(AlarmRequest request) {
        String content = request.getContent();
        String title = request.getTitle();
        boolean atAll = request.isAtAll();
        Set<String> phones = request.getPhones();
        Message message = new DingTalkMarkDown(new DingTalkMarkDown.MarkDown(title, content));
        if (atAll) {
            message.setAt(new Message.At(true));
        } else if (phones != null && !phones.isEmpty()) {
            message.setAt(new Message.At(phones));
        }
        return message;
    }

    @Override
    public String unionId() {
        MessageSource messageSource = new MessageSource();
        messageSource.setMessageType(MessageSubType.MARKDOWN.getCode());
        messageSource.setAlarmType(AlarmType.DINGTALK.getType());
        return messageSource.convert();
    }
}
