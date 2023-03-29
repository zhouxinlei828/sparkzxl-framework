package com.github.sparkzxl.sms.entity;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * description: 短信结果
 *
 * @author zhouxinlei
 * @since 2022-11-08 17:06:58
 */
@Builder
@Data
public class SmsResult implements Serializable {

    private static final long serialVersionUID = 1111161020134249934L;

    /**
     * 是否成功
     */
    private String code;
    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 实际响应体
     * <p>
     * 可自行转换为 SDK 对应的 SendSmsResponse
     */
    private String response;

}
