package com.github.sparkzxl.sms.response;

import com.github.sparkzxl.sms.response.common.SmsResp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2022-01-03 17:18:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class SmsSendDetail extends SmsResp {

    private static final long serialVersionUID = 3694883007373925413L;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 模板id
     */
    private String templateId;

    /**
     * 用户接收时间
     */
    private String receiveDate;

    /**
     * 发送状态
     */
    private Long sendStatus;

    private String description;

}
