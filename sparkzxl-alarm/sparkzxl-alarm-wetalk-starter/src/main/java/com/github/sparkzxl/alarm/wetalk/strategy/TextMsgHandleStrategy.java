package com.github.sparkzxl.alarm.wetalk.strategy;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmChannel;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.strategy.MessageSource;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;
import com.github.sparkzxl.alarm.wetalk.entity.WeText;
import com.google.common.collect.Sets;

import java.util.Set;

import static com.github.sparkzxl.alarm.constant.AlarmConstant.WETALK_AT_ALL;

/**
 * description: 企业微信文本消息
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:32:43
 */
public class TextMsgHandleStrategy implements MsgHandleStrategy {

    @Override
    public MsgType newInstance(AlarmRequest request) {
        String content = request.getContent();
        boolean atAll = request.isAtAll();
        Set<String> phones = request.getPhones();
        WeText.Text text = new WeText.Text(content);
        WeText weText = new WeText(text);
        if (atAll) {
            text.setMentioned_mobile_list(Sets.newHashSet(WETALK_AT_ALL));
        } else if (phones != null && !phones.isEmpty()) {
            text.setMentioned_mobile_list(phones);
        }
        return weText;
    }

    @Override
    public String unionId() {
        MessageSource messageSource = new MessageSource();
        messageSource.setMessageType(MessageSubType.TEXT.getCode());
        messageSource.setAlarmType(AlarmChannel.WETALK.getType());
        return messageSource.convert();
    }
}
