package com.github.sparkzxl.alarm.properties;

import lombok.Data;

/**
 * description: 告警配置
 *
 * @author zhouxinlei
 * @since 2022-05-24 09:50:42
 */
@Data
public class AlarmConfig {

    /**
     * 机器人id
     */
    protected String robotId = "default";
    /**
     * 请求地址前缀-选填
     */
    private String robotUrl;
    /**
     * 获取 access_token, 必填
     * 填写机器人设置中 webhook access_token | key后面的值
     */
    private String tokenId;
    /**
     * 选填, 签名秘钥。 需要验签时必填(钉钉机器人提供)
     */
    private String secret;

    /**
     * 选填, 是否开启异步处理, 默认： false
     */
    private boolean async;
}
