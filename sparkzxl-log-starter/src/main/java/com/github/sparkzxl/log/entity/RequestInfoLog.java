package com.github.sparkzxl.log.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "RequestLog对象", description = "请求日志实体类")
public class RequestInfoLog implements Serializable {

    private static final long serialVersionUID = -4039189147020220726L;

    @ApiModelProperty(value = "业务分类")
    private String category;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "请求IP")
    private String requestIp;

    @ApiModelProperty(value = "请求接口")
    private String requestUrl;

    @ApiModelProperty(value = "请求类型:GET:GET请求;POST:POST请求;PUT:PUT请求;DELETE:DELETE请求;PATCH:PATCH请求;TRACE:TRACE请求;HEAD:HEAD请求;OPTIONS:OPTIONS请求;")
    private String httpMethod;

    @ApiModelProperty(value = "请求类方法")
    private String classMethod;

    @ApiModelProperty(value = "请求报文")
    private String requestParams;

    @ApiModelProperty(value = "响应报文")
    private String result;

    @ApiModelProperty(value = "异常信息")
    private String errorMsg;

    @ApiModelProperty(value = "抛出异常类")
    private String throwExceptionClass;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "完成时间")
    private LocalDateTime finishTime;

    @ApiModelProperty(value = "consumingTime")
    private Long consumingTime;

    @ApiModelProperty(value = "tenantId")
    private String tenantId;

}
