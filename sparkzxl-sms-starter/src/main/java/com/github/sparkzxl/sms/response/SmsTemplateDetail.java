package com.github.sparkzxl.sms.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * description: 模板签名信息
 *
 * @author zhouxinlei
 * @date 2022-01-03 18:58:41
 */
@Data
@Accessors(chain = true)
public class SmsTemplateDetail implements Serializable {

    private static final long serialVersionUID = -7684389210947487259L;

    /**
     * 模板id
     */
    private String templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板内容
     */
    private String templateContent;

    /**
     * 模板状态:0表示审核通过,1表示审核中,-1：表示审核未通过或审核失败
     */
    private Integer templateStatus;

    /**
     * 模板签名创建时间
     */
    private String createDate;

    /**
     * 审核备注
     */
    private String reason;

}
