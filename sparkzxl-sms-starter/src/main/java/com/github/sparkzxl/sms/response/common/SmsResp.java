package com.github.sparkzxl.sms.response.common;

import lombok.Data;

import java.io.Serializable;

/**
 * description: 发送状态结果
 *
 * @author zhouxinlei
 * @date 2022-01-03 17:09:13
 */
@Data
public class SmsResp<T> implements Serializable {

    private static final long serialVersionUID = 4225303557371738036L;

    public String code;

    public String message;

    private String requestId;

    private T data;

}
