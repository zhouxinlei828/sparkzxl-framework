package com.github.sparkzxl;

import com.github.sparkzxl.service.AlarmWarnService;
import org.springframework.util.StringUtils;

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

    public static void execute(String message) {
        if (!StringUtils.isEmpty(message)) {
            for (AlarmWarnService alarmWarnService : getServiceList()) {
                alarmWarnService.send(message);
            }
        }
    }
}
