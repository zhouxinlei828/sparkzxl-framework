package com.github.sparkzxl.alarm.feishutalk.strategy;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.feishutalk.entity.FeiShuTalkText;
import com.github.sparkzxl.alarm.feishutalk.entity.Message;
import com.github.sparkzxl.alarm.strategy.MessageSource;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * description: 飞书文本消息
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:32:43
 */
public class TextMsgHandleStrategy implements MsgHandleStrategy {

    @Override
    public MsgType getMessage(AlarmRequest request) {
        boolean atAll = request.isAtAll();
        FeiShuTalkText.Content content = new FeiShuTalkText.Content(request.getContent());
        FeiShuTalkText feiShuTalkText = new FeiShuTalkText(content);
        if (atAll) {
            List<Message.FeiShuAt> atList = Lists.newArrayList(new Message.FeiShuAt("all", "所有人"));
            feiShuTalkText.setAtList(atList);
        }
        return feiShuTalkText;
    }

    @Override
    public String unionId() {
        MessageSource messageSource = new MessageSource();
        messageSource.setMessageType(MessageSubType.TEXT.getCode());
        messageSource.setAlarmType(AlarmType.FEISHU.getType());
        return messageSource.convert();
    }
}
