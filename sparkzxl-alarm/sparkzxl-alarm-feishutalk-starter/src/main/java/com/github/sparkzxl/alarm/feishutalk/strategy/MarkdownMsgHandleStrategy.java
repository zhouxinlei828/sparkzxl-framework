package com.github.sparkzxl.alarm.feishutalk.strategy;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmChannel;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.feishutalk.entity.FeiShuTalkMarkdown;
import com.github.sparkzxl.alarm.strategy.MessageSource;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * description: 飞书markdown消息
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:32:43
 */
public class MarkdownMsgHandleStrategy implements MsgHandleStrategy {

    @Override
    public MsgType newInstance(AlarmRequest request) {
        String content = request.getContent();
        FeiShuTalkMarkdown message = new FeiShuTalkMarkdown();
        FeiShuTalkMarkdown.Card card = new FeiShuTalkMarkdown.Card();
        card.setConfig(new FeiShuTalkMarkdown.Config(true));
        FeiShuTalkMarkdown.Header header = new FeiShuTalkMarkdown.Header();
        header.setTitle(new FeiShuTalkMarkdown.Header.Title("plain_text", request.getTitle()));
        card.setHeader(header);
        List<FeiShuTalkMarkdown.Element> elements = Lists.newArrayList();
        FeiShuTalkMarkdown.Element element = new FeiShuTalkMarkdown.Element();
        element.setTag("markdown");
        element.setContent(content);
        elements.add(element);
        card.setElements(elements);
        message.setCard(card);
        return message;
    }

    @Override
    public String unionId() {
        MessageSource messageSource = new MessageSource();
        messageSource.setMessageType(MessageSubType.MARKDOWN.getCode());
        messageSource.setAlarmType(AlarmChannel.FEISHU.getType());
        return messageSource.convert();
    }
}
