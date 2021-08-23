package com.github.sparkzxl;

import com.github.sparkzxl.entity.AlarmLogInfo;
import com.github.sparkzxl.service.LogAlarmWarnService;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 日志静态工厂执行器
 *
 * @author zhoux
 * @date 2021-08-22 09:20:43
 */
public class AlarmLogFactoryExecute {

    private static List<LogAlarmWarnService> serviceList = new ArrayList<>();

    public AlarmLogFactoryExecute(List<LogAlarmWarnService> alarmLogWarnServices) {
        serviceList = alarmLogWarnServices;
    }

    public static void addAlarmLogWarnService(LogAlarmWarnService alarmLogWarnService) {
        serviceList.add(alarmLogWarnService);
    }

    public static List<LogAlarmWarnService> getServiceList() {
        return serviceList;
    }

    public static void execute(AlarmLogInfo alarmLogInfo, Throwable throwable) {
        if (!ObjectUtils.isEmpty(alarmLogInfo)) {
            for (LogAlarmWarnService logAlarmWarnService : getServiceList()) {
                logAlarmWarnService.send(alarmLogInfo, throwable);
            }
        }
    }
}
