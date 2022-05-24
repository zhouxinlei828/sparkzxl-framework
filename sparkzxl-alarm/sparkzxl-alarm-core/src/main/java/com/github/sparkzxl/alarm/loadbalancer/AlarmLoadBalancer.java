package com.github.sparkzxl.alarm.loadbalancer;

import com.github.sparkzxl.alarm.properties.AlarmConfig;

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
    AlarmConfig choose(List<AlarmConfig> alarmConfigList);


    /**
     * 指定机器人选择配置
     *
     * @param robotId         机器人id {@link AlarmConfig#getRobotId()}
     * @param alarmConfigList 告警配置列表
     * @return AlarmConfig
     */
    AlarmConfig chooseDesignatedRobot(String robotId, List<AlarmConfig> alarmConfigList);
}
