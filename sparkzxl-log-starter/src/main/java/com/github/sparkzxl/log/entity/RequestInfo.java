package com.github.sparkzxl.log.entity;

import lombok.Data;

/**
 * description: 请求日志实体类
 *
 * @author zhouxinlei
 */
@Data
public class RequestInfo {

    /**
     * 请求IP
     */
    private String ip;
    /**
     * 请求路径
     */
    private String url;
    /**
     * http方法类型
     */
    private String httpMethod;
    /**
     * 请求类方法
     */
    private String classMethod;
    /**
     * 请求参数
     */
    private Object requestParams;
    /**
     * 请求结果
     */
    private Object result;

    /**
     * 请求耗时
     */
    private String timeCost;
}
