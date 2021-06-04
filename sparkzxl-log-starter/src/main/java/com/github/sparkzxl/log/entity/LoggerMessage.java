package com.github.sparkzxl.log.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-06-04 09:19
 */
@Data
@AllArgsConstructor
public class LoggerMessage {

    private String timestamp;

    private String application;

    private String level;

    private String body;

    private String className;

    private String threadName;



}
