package com.github.sparkzxl.log.realtime.disruptor;


import com.lmax.disruptor.EventHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * description: 进程日志事件处理器
 *
 * @author zhouxinlei
 * @date 2021-06-04 09:45:47
 */
public class LoggerEventHandler implements EventHandler<LoggerEvent> {

    private SimpMessagingTemplate messagingTemplate;

    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onEvent(LoggerEvent stringEvent, long l, boolean b) {
        messagingTemplate.convertAndSend("/topic/pullLogger", stringEvent.getLog());
    }
}
