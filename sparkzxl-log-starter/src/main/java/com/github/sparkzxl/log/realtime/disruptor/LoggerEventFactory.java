package com.github.sparkzxl.log.realtime.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * description: 进程日志事件工厂类
 *
 * @author zhouxinlei
 * @date 2021-06-04 09:45:30
 */
public class LoggerEventFactory implements EventFactory<LoggerEvent> {
    @Override
    public LoggerEvent newInstance() {
        return new LoggerEvent();
    }
}
