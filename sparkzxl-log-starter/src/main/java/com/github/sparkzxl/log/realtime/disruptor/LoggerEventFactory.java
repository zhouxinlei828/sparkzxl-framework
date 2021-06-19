package com.github.sparkzxl.log.realtime.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * description: 进程日志事件工厂类
 *
 * @author zhouxinlei
 */
public class LoggerEventFactory implements EventFactory<LoggerEvent> {
    @Override
    public LoggerEvent newInstance() {
        return new LoggerEvent();
    }
}
