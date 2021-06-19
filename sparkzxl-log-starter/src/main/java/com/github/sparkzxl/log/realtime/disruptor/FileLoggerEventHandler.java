package com.github.sparkzxl.log.realtime.disruptor;

import com.lmax.disruptor.EventHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * description: 文件日志事件处理器
 *
 * @author zhouxinlei
 */
public class FileLoggerEventHandler implements EventHandler<FileLoggerEvent> {

    private SimpMessagingTemplate messagingTemplate;

    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onEvent(FileLoggerEvent fileLoggerEvent, long l, boolean b) {
        messagingTemplate.convertAndSend("/topic/pullFileLogger", fileLoggerEvent.getLog());
    }
}
