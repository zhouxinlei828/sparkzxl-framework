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

    /**
     * 发送Markdown消息
     *
     * @param title   Markdown标题
     * @param message Markdown消息
     * @throws Exception 异常
     */
    protected abstract void doSendMarkdown(String title, String message) throws Exception;

    /**
     * 发送文本消息
     *
     * @param message 文本消息
     * @throws Exception 异常
     */
    protected abstract void doSendText(String message) throws Exception;
}
