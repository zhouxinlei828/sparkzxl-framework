package com.github.sparkzxl.alarm.support;

/**
 * description: AlarmIdGenerator
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:02:22
 */
public interface AlarmIdGenerator {

    /**
     * 告警id
     *
     * @return String
     */
    String nextAlarmId();
}
