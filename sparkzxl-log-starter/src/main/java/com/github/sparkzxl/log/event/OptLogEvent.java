package com.github.sparkzxl.log.event;

import com.github.sparkzxl.log.entity.OptLogRecordDetail;
import org.springframework.context.ApplicationEvent;

/**
 * description: 操作日志事件
 *
 * @author zhouxinlei
 */
public class OptLogEvent extends ApplicationEvent {

    public OptLogEvent(OptLogRecordDetail source) {
        super(source);
    }
}
