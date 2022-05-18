package com.github.sparkzxl.alarm.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> phones = new ArrayList<>();
    /**
     * 艾特成员
     */
    private boolean atAll = false;

}
