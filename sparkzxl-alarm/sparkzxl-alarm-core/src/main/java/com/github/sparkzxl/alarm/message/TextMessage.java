package com.github.sparkzxl.alarm.message;

import com.github.sparkzxl.alarm.entity.AlarmRequest;

/**
 * description: 默认文本消息
 *
 * @author zhouxinlei
 * @since 2022-05-18 11:31:12
 */
public class TextMessage implements CustomMessage {

    @Override
    public String message(AlarmRequest request) {
        String content = request.getContent();
        return String.format("【Alarm通知】 %s\n- 内容: %s.", request.getTitle(), content);
    }
}
