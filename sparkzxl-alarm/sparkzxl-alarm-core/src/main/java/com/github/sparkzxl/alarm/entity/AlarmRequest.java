package com.github.sparkzxl.alarm.entity;

import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import lombok.Data;

/**
 * description: 告警请求对象
 *
 * @author zhouxinlei
 * @since 2022-05-18 09:56:06
 */
@Data
public class AlarmRequest {

    /**
     * 消息内容
     */
    private String content;
    /**
     * 标题(dingTalk-markdown)
     */
    private String title;
    /**
     * 艾特成员信息
     */
    private Set<String> phones = Sets.newHashSet();
    /**
     * 艾特成员
     */
    private boolean atAll = false;

    /**
     * 变量参数
     */
    private Map<String, Object> variables;

}
