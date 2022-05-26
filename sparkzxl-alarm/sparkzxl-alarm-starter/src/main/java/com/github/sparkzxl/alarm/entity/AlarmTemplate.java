package com.github.sparkzxl.alarm.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * description: 告警模板实体类
 *
 * @author zhouxinlei
 * @since 2021-12-28 11:06
 */
@Data
public class AlarmTemplate implements Serializable {

    private static final long serialVersionUID = -3287747093418563016L;
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
}
