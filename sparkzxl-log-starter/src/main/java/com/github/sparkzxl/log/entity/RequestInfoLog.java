package com.github.sparkzxl.log.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * description: 请求日志实体类
 *
 * @author zhouxinlei
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RequestInfoLog implements Serializable {

    private static final long serialVersionUID = -4039189147020220726L;

    /**
     * 业务分类
     */
    private String category;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * 请求接口
     */
    private String requestUrl;

    /**
     * 请求类型:GET:GET请求;POST:POST请求;PUT:PUT请求;DELETE:DELETE请求;PATCH:
     * PATCH请求;TRACE:TRACE请求;HEAD:HEAD请求;OPTIONS:OPTIONS请求;
     */
    private String httpMethod;

    /**
     * 请求类方法
     */
    private String classMethod;

    /**
     * 请求报文
     */
    private String requestParams;

    /**
     * 响应报文
     */
    private String result;

    /**
     * 异常信息
     */
    private String errorMsg;

    /**
     * 抛出异常类
     */
    private String throwExceptionClass;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 耗时时间
     */
    private String consumingTime;

    /**
     * 租户
     */
    private String tenantId;

}
