package com.github.sparkzxl.sms.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * description: 短信发送记录与回执信息
 *
 * @author zhouxinlei
 * @since 2022-05-26 09:44:50
 */
@Accessors(chain = true)
@Data
public class SmsSendRecord {

    /**
     * id
     */
    private Long id;

    /**
     * 短信请求发送id
     */
    private String smsReqId;

    /**
     * 回执id
     */
    public String bizId;

    /**
     * 短信模板id
     */
    private String templateId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 渠道商Id
     */
    private Integer supplierId;

    /**
     * 渠道商名字
     */
    private String supplierName;

    /**
     * 短信发送的内容
     */
    private String msgContent;

    /**
     * 回执信息
     */
    private String reportContent;

    /**
     * 短信执行状态
     */
    private Integer status;

    /**
     * 短信执行状态名称
     */
    private String statusName;

    /**
     * 发送时间
     */
    private LocalDateTime sendDateTime;

}
