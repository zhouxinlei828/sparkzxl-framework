package com.github.sparkzxl.alarm.enums;

/**
 * description: 告警类型
 *
 * @author zhouxinlei
 * @since 2022-05-18 10:01:59
 */
public enum AlarmType {

    /**
     * 钉钉
     */
    DINGTALK("dingtalk", "https://oapi.dingtalk.com/robot/send?access_token=", true),
    /**
     * 企业微信
     */
    WETALK("wetalk", "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=", true),
    ;

    private final String type;
    private final String robotUrl;
    /**
     * 是否开启
     */
    private final boolean enabled;

    AlarmType(String type, String robotUrl, boolean enabled) {
        this.type = type;
        this.robotUrl = robotUrl;
        this.enabled = enabled;
    }

    public String getType() {
        return type;
    }

    public String getRobotUrl() {
        return robotUrl;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
