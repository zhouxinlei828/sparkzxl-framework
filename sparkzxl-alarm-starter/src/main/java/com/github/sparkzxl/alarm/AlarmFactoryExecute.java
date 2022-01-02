package com.github.sparkzxl.alarm;

import com.github.sparkzxl.alarm.entity.NotifyMessage;
import com.github.sparkzxl.alarm.service.AlarmWarnService;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 静态工厂执行器
 *
 * @author zhoux
 */
public class AlarmFactoryExecute {

    private static List<AlarmWarnService> serviceList = new ArrayList<>();

    public AlarmFactoryExecute(List<AlarmWarnService> alarmLogWarnServices) {
        serviceList = alarmLogWarnServices;
    }

    public static void addAlarmLogWarnService(AlarmWarnService alarmLogWarnService) {
        serviceList.add(alarmLogWarnService);
    }

    public static List<AlarmWarnService> getServiceList() {
        return serviceList;
    }

    public static void execute(NotifyMessage notifyMessage) {
        for (AlarmWarnService alarmWarnService : getServiceList()) {
            alarmWarnService.send(notifyMessage);
        }
    }
}
