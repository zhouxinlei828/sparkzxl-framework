package com.github.sparkzxl.alarm.constant;

/**
 * description: 告警常量类
 *
 * @author zhouxinlei
 * @since 2022-05-25 16:03:44
 */
public class AlarmConstant {

    /**
     * 默认告警处理变量参数bean name
     */
    public static final String DEFAULT_ALARM_VARIABLES_HANDLER_BEAN_NAME = "defaultAlarmVariablesHandler";

    public static final String MARKDOWN_HTTP_STATUS_TEMPLATE = "- HTTP状态：#{[state]}\r\n";

    public static final String TEXT_HTTP_STATUS_TEMPLATE = "HTTP状态：#{[state]}\r\n";

    public static final String MARKDOWN_ERROR_TEMPLATE = "- <font color=\"#F37335\">异常信息:</font>\n" +
            "```java\n" +
            "#{[exception]}\n" +
            "```\r\n";

    public static final String TEXT_ERROR_TEMPLATE = "\n异常信息:\n" +
            "#{[exception]}\r\n";


}
