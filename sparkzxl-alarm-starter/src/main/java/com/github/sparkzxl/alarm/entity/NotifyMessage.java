package com.github.sparkzxl.alarm.entity;

import com.github.sparkzxl.alarm.constant.enums.MessageTye;
import lombok.Data;

import java.io.Serializable;

/**
 * description: 消息通知
 *
 * @author zhouxinlei
 * @date 2022-01-02 20:01:36
 */
@Data
public class NotifyMessage implements Serializable {

    private MessageTye messageTye;

    private String title;

    private String message;
}
