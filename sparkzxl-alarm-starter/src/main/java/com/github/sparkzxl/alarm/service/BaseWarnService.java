package com.github.sparkzxl.alarm.service;

import com.github.sparkzxl.alarm.constant.enums.MessageTye;
import com.github.sparkzxl.alarm.entity.NotifyMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * description:
 *
 * @author zhoux
 */
@Slf4j
public abstract class BaseWarnService implements AlarmWarnService {

    @Override
    public void send(NotifyMessage notifyMessage) {
        try {
            if (notifyMessage.getMessageTye().equals(MessageTye.TEXT)) {
                doSendText(notifyMessage.getMessage());
            } else if (notifyMessage.getMessageTye().equals(MessageTye.MARKDOWN)) {
                doSendMarkdown(notifyMessage.getTitle(), notifyMessage.getMessage());
            }
        } catch (Exception e) {
            log.error("send warn message error", e);
        }
    }

    protected abstract void doSendMarkdown(String title, String message) throws Exception;

    protected abstract void doSendText(String message) throws Exception;
}
