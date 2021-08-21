package com.github.sparkzxl.factory;


import com.github.sparkzxl.service.LogAlarmWarnService;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 日志告警工厂
 *
 * @author zhoux
 * @date 2021-08-21 12:13:42
 */
public class AlarmLogWarnServiceFactory {

    private static List<LogAlarmWarnService> serviceList = new ArrayList<>();

    public AlarmLogWarnServiceFactory() {
    }

    public AlarmLogWarnServiceFactory(List<LogAlarmWarnService> alarmLogWarnServices) {
        serviceList = alarmLogWarnServices;
    }

    public static void setAlarmLogWarnService(LogAlarmWarnService alarmLogWarnService) {
        serviceList.add(alarmLogWarnService);
    }

    public static List<LogAlarmWarnService> getServiceList () {
        return serviceList;
    }
}
