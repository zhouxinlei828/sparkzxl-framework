package com.github.sparkzxl.alarm.enums;

/**
 * description: 告警渠道
 *
 * @author zhouxinlei
 * @since 2022-05-18 10:01:59
 */
public enum AlarmChannel {

    /**
     * 钉钉
     */
    DINGTALK("dingtalk", "https://oapi.dingtalk.com/robot/send?access_token=", true),
    /**
     * 企业微信
     */
    WETALK("wetalk", "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=", true),
    /**
     * 飞书
     */
    FEISHU("feishu", "https://open.feishu.cn/open-apis/bot/v2/hook/", true),
    ;

    private final String type;
    private final String robotUrl;
    /**
     * 是否开启
     */
    private final boolean enabled;

    AlarmChannel(String type, String robotUrl, boolean enabled) {
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
