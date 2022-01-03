package com.github.sparkzxl.sms.response;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * description: 发送短信结果
 *
 * @author zhouxinlei
 * @date 2022-01-03 14:34:09
 */
@Data
@Accessors(chain = true)
public class SendSmsResult implements Serializable {

    private static final long serialVersionUID = 1150831189389516749L;

    public String code;

    public String message;

    private String bizId;

    private String phoneNumber;

}
