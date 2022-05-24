package com.github.sparkzxl.alarm.loadbalancer;

import com.github.sparkzxl.alarm.properties.AlarmConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * description: 随机选择算法
 *
 * @author zhouxinlei
 * @since 2022-05-24 10:22:07
 */
public class RandomAlarmLoadBalancer implements AlarmLoadBalancer {

    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public AlarmConfig choose(List<AlarmConfig> alarmConfigList) {
        if (CollectionUtils.isEmpty(alarmConfigList)) {
            return null;
        }
        return alarmConfigList.get(Math.abs(index.getAndAdd(1) % alarmConfigList.size()));
    }

    @Override
    public AlarmConfig chooseDesignatedRobot(String robotId, List<AlarmConfig> alarmConfigList) {
        if (CollectionUtils.isEmpty(alarmConfigList)) {
            return null;
        }
        List<AlarmConfig> configList = alarmConfigList.stream().filter(x -> StringUtils.equals(x.getRobotId(), robotId))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(configList)) {
            return null;
        }
        return configList.get(Math.abs(index.getAndAdd(1) % configList.size()));
    }
}
