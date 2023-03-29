package com.github.sparkzxl.alarm.strategy;

import java.text.MessageFormat;
import lombok.Data;

/**
 * description: 消息类型来源
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:25:16
 */
@Data
public class MessageSource {

    private String alarmType;

    private String messageType;

    public String convert() {
        return MessageFormat.format("{0}#{1}", this.alarmType, this.messageType);
    }
}
