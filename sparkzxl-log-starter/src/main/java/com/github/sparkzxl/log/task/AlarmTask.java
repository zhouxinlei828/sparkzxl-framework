package com.github.sparkzxl.log.task;

import cn.hutool.cron.task.Task;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.send.AlarmClient;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.log.queue.AlarmTaskInfo;
import com.github.sparkzxl.log.queue.AlarmTaskQueue;
import org.apache.commons.lang3.StringUtils;

/**
 * description: 告警任务
 *
 * @author zhouxinlei
 * @since 2022-12-26 13:30:23
 */
public class AlarmTask implements Task {

    @Override
    public void execute() {
        AlarmTaskInfo alarmTask = AlarmTaskQueue.getQueue().consume();
        if (alarmTask != null) {
            AlarmClient alarmClient = SpringContextUtils.getBean(AlarmClient.class);
            if (StringUtils.isBlank(alarmTask.getRobotId())) {
                alarmClient.send(MessageSubType.MARKDOWN, alarmTask.getAlarmRequest());
            } else {
                alarmClient.designatedRobotSend(alarmTask.getRobotId(), MessageSubType.MARKDOWN, alarmTask.getAlarmRequest());
            }
        }
    }
}
