package com.github.sparkzxl.sms.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * description: 发送短信实体类
 *
 * @author zhouxinlei
 * @date 2022-01-03 12:39:34
 */
@Data
public class SendSmsReq implements Serializable {

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 手机号列表
     */
    private List<String> phoneNumberList;

    /**
     * 签名
     */
    private String sign;

    /**
     * 短信模板id
     */
    private String templateId;

    /**
     * 可选:模版描述
     */
    private String desc;

    /**
     * 可选:短信模板中的变量
     */
    private Map<String, String> templateParams;

}
