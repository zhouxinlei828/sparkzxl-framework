package com.github.sparkzxl.alarm.wetalk.strategy;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.wetalk.entity.WeMarkdown;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;
import com.github.sparkzxl.alarm.strategy.MessageSource;

/**
 * description: 企业微信富文本消息
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:32:43
 */
public class MarkdownMsgHandleStrategy implements MsgHandleStrategy {

    @Override
    public MsgType getMessage(AlarmRequest request) {
        String content = request.getContent();
        WeMarkdown.Markdown markdown = new WeMarkdown.Markdown(content);
        return new WeMarkdown(markdown);
    }

    @Override
    public String unionId() {
        MessageSource messageSource = new MessageSource();
        messageSource.setMessageType(MessageSubType.MARKDOWN.name());
        messageSource.setAlarmType(AlarmType.WETALK.getType());
        return messageSource.convert();
    }
}
