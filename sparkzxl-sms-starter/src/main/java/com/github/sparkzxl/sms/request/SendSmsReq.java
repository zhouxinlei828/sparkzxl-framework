package com.github.sparkzxl.sms.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * description: 发送短信实体类
 *
 * @author zhouxinlei
 * @since 2022-01-03 12:39:34
 */
@Data
public class SendSmsReq implements Serializable {

    private static final long serialVersionUID = -8200484562245141033L;
    /**
     * 手机号列表
     */
    private Set<String> phones;

    /**
     * 签名
     */
    private String sign;

    /**
     * 短信模板id
     */
    private String templateId;

    /**
     * 发送文案
     */
    private String templateContent;

    /**
     * 可选:模版描述
     */
    private String desc;

    /**
     * 可选:短信模板中的变量
     */
    private TreeMap<String, Object> templateParams;

}
