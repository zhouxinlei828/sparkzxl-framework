package com.github.sparkzxl.log.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * description: 请求日志实体类
 *
 * @author zhouxinlei
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpLogInfo {

    /**
     * 请求IP
     */
    private String ip;
    /**
     * 请求接口
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
     * 用户id
     */
    private String userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 请求参数
     */
    private Object params;

    /**
     * 请求结果
     */
    private Object result;

    /**
     * 错误原因
     */
    private String error;

    /**
     * exception中包含的信息
     */
    private String errorMsg;

    private String throwExceptionClass;

    /**
     * 请求耗时
     */
    private String timeCost;

}
