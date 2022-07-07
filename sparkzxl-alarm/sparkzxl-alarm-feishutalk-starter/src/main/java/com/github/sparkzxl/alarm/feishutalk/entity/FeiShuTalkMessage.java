package com.github.sparkzxl.alarm.feishutalk.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmChannel;
import com.github.sparkzxl.core.jackson.JsonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * description: FeiShuTalk请求体
 *
 * @author zhouxinlei
 * @since 2022-05-18 13:48:06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeiShuTalkMessage extends MsgType {

    private static final long serialVersionUID = -452343710497506650L;

    @JsonProperty(value = "msg_type")
    private String msgtype;

    public FeiShuTalkMessage() {
        setAlarmChannel(AlarmChannel.FEISHU);
    }

    @Override
    public String toJson() {
        return JsonUtil.toJson(this);
    }
}