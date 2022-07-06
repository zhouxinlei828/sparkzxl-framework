package com.github.sparkzxl.alarm.dingtalk.strategy;

import com.github.sparkzxl.alarm.dingtalk.entity.DingTalkActionCard;
import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;
import com.github.sparkzxl.alarm.strategy.MessageSource;

/**
 * description: 钉钉卡片类型消息
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:32:43
 */
public class ActionCardMsgHandleStrategy implements MsgHandleStrategy {

    @Override
    public MsgType getMessage(AlarmRequest request) {
        return new DingTalkActionCard();
    }

    @Override
    public String unionId() {
        MessageSource messageSource = new MessageSource();
        messageSource.setMessageType(MessageSubType.ACTION_CARD.getCode());
        messageSource.setAlarmType(AlarmType.DINGTALK.getType());
        return messageSource.convert();
    }
}
