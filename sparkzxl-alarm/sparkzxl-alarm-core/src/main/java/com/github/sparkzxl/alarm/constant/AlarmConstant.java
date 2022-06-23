package com.github.sparkzxl.alarm.constant;

/**
 * description: 告警常量类
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:21:29
 */
public interface AlarmConstant {

    String ALARM_PREFIX = "A";
    /**
     * 默认线程池中线程名称前缀
     */
    String DEFAULT_THREAD_NAME_PREFIX = "alarm-";
    /**
     * 告警配置属性前缀
     */
    String ALARM_CONFIG_PREFIX = "spring.alarm";
    /**
     * 告警线程池配置属性前缀
     */
    String ALARM_THREAD_CONFIG_PREFIX = "spring.alarm.executor-pool";


    /**
     * bean name
     */
    String TEXT_MESSAGE = "textMessage";
    /**
     * bean name
     */
    String MARKDOWN_MESSAGE = "markDownMessage";

    String NEW_LINE = "\r\n";
    String SPOT_SEPERATOR = ".";

    String WETALK_AT_ALL = "@all";

    String ALARM_THREAD_POOL_EXECUTOR = "alarmThreadPoolExecutor";
}