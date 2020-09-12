package com.github.sparkzxl.log.event;

import com.github.sparkzxl.log.entity.OperateLog;
import org.springframework.context.ApplicationEvent;

/**
 * description: 系统日志事件
 *
 * @author: zhouxinlei
 * @date: 2020-07-12 00:01:24
 */
public class SysLogEvent extends ApplicationEvent {
    public SysLogEvent(OperateLog source) {
        super(source);
    }
}
