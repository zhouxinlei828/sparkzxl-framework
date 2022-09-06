package com.github.sparkzxl.alarm.dingtalk.strategy;

import com.github.sparkzxl.alarm.dingtalk.entity.DingTalkLink;
import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmChannel;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.strategy.MessageSource;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;

/**
 * description: 钉钉link消息
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:32:43
 */
public class MsgLinkHandleStrategy implements MsgHandleStrategy {

    @Override
    public MsgType newInstance(AlarmRequest request) {
        return new DingTalkLink();
    }

    @Override
    public String unionId() {
        MessageSource messageSource = new MessageSource();
        messageSource.setMessageType(MessageSubType.LINK.getCode());
        messageSource.setAlarmType(AlarmChannel.DINGTALK.getType());
        return messageSource.convert();
    }
}
