package com.github.sparkzxl.alarm.message;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import java.text.MessageFormat;
import java.util.Set;

/**
 * description: 默认markdown消息格式
 *
 * @author zhouxinlei
 * @since 2022-05-18 11:32:17
 */
public class MarkDownMessageTemplate implements MessageTemplate {

    @Override
    public String message(AlarmRequest request) {
        String content = request.getContent();
        String title = request.getTitle();
        Set<String> phones = request.getPhones();
        // markdown在text内容里需要有@手机号
        StringBuilder text = new StringBuilder(title);
        if (phones != null && !phones.isEmpty()) {
            for (String phone : phones) {
                text.append("@").append(phone);
            }
        }
        return MessageFormat.format(
                "# 【{0}】\n {1}",
                text, content);
    }
}
