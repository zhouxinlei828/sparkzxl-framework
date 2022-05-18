package com.github.sparkzxl.alarm.message;

import cn.hutool.core.text.StrFormatter;
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
        return StrFormatter.format("【Alarm通知】 {0}\n- 内容: {1}.", content);
    }
}
