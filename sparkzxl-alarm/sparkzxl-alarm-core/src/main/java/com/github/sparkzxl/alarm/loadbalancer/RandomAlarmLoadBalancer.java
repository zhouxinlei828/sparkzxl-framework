package com.github.sparkzxl.alarm.loadbalancer;

import com.github.sparkzxl.alarm.properties.AlarmProperties;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * description: 随机选择算法
 *
 * @author zhouxinlei
 * @since 2022-05-24 10:22:07
 */
public class RandomAlarmLoadBalancer implements AlarmLoadBalancer {

    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public AlarmProperties.AlarmConfig choose(List<AlarmProperties.AlarmConfig> alarmConfigList) {
        if (CollectionUtils.isEmpty(alarmConfigList)) {
            return null;
        }
        return alarmConfigList.get(Math.abs(index.getAndAdd(1) % alarmConfigList.size()));
    }

    @Override
    public AlarmProperties.AlarmConfig chooseDesignatedRobot(String robotId, List<AlarmProperties.AlarmConfig> alarmConfigList) {
        if (CollectionUtils.isEmpty(alarmConfigList)) {
            return null;
        }
        List<AlarmProperties.AlarmConfig> configList = alarmConfigList.stream().filter(x -> StringUtils.equals(x.getRobotId(), robotId))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(configList)) {
            return null;
        }
        return configList.get(Math.abs(index.getAndAdd(1) % configList.size()));
    }
}
