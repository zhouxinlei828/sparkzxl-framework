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
     * bean name
     */
    String TEXT_MESSAGE = "textMessage";
    /**
     * bean name
     */
    String MARKDOWN_MESSAGE = "markDownMessage";
    /**
     * bean name
     */
    String DINGER_EXECUTOR = "dingerExecutor";
    /**
     * 自定义restTemplate名称
     */
    String DINGER_REST_TEMPLATE = "dingerRestTemplate";
    String DINGER_HTTP_CLIENT = "dingerHttpClient";

    String NEW_LINE = "\r\n";
    String SPOT_SEPERATOR = ".";

    String DINGER_PROP_PREFIX = "spring.dinger";
    String DINGER_PROPERTIES_PREFIX = DINGER_PROP_PREFIX + SPOT_SEPERATOR;

    public static final String WETALK_AT_ALL = "@all";
}