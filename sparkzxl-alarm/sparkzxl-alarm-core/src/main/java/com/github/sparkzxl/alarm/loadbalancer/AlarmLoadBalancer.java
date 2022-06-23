package com.github.sparkzxl.alarm.loadbalancer;

import com.github.sparkzxl.alarm.properties.AlarmProperties;

import java.util.List;

/**
 * description: 告警负载均衡
 *
 * @author zhouxinlei
 * @since 2022-05-24 10:12:38
 */
public interface AlarmLoadBalancer {


    /**
     * 选择配置
     *
     * @param alarmConfigList 告警配置列表
     * @return AlarmConfig
     */
    AlarmProperties.AlarmConfig choose(List<AlarmProperties.AlarmConfig> alarmConfigList);


    /**
     * 指定机器人选择配置
     *
     * @param robotId         机器人id {@link com.github.sparkzxl.alarm.properties.AlarmProperties.AlarmConfig#getRobotId()}
     * @param alarmConfigList 告警配置列表
     * @return AlarmConfig
     */
    AlarmProperties.AlarmConfig chooseDesignatedRobot(String robotId, List<AlarmProperties.AlarmConfig> alarmConfigList);
}
