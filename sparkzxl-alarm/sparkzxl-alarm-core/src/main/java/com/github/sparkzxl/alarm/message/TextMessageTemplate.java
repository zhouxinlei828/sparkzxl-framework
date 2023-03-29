package com.github.sparkzxl.alarm.message;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import java.text.MessageFormat;

/**
 * description: 默认文本消息
 *
 * @author zhouxinlei
 * @since 2022-05-18 11:31:12
 */
public class TextMessageTemplate implements MessageTemplate {

    @Override
    public String message(AlarmRequest request) {
        return MessageFormat.format("{0}\n {1}.", request.getTitle(), request.getContent());
    }
}
