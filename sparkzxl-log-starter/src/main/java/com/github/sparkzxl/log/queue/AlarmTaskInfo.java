package com.github.sparkzxl.log.queue;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * description: 告警任务信息
 *
 * @author zhouxinlei
 * @since 2022-12-26 10:16:34
 */
@Getter
@Setter
public class AlarmTaskInfo {
    private final String robotId;
    private final AlarmRequest alarmRequest;

    public AlarmTaskInfo(String robotId,
                         AlarmRequest alarmRequest) {
        this.robotId = robotId;
        this.alarmRequest = alarmRequest;
    }
}
