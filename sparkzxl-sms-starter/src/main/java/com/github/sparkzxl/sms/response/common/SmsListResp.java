package com.github.sparkzxl.sms.response.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * description: 发送状态结果列表
 *
 * @author zhouxinlei
 * @date 2022-01-03 17:09:13
 */
@Data
public class SmsListResp<T> implements Serializable {

    private static final long serialVersionUID = -7086918666830813740L;

    public String code;

    public String message;

    private String requestId;

    private List<T> dataList;
}
