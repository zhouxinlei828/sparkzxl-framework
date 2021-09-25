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
 * @date 2021-09-25 16:04:19
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
    private Integer userId;

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "请求IP")
    private String ip;

    @ApiModelProperty(value = "请求路径")
    private String requestUrl;

    @ApiModelProperty(value = "请求类方法")
    private String classMethod;

    @ApiModelProperty(value = "请求报文")
    private String requestParams;

    @ApiModelProperty(value = "响应报文")
    private String responseResult;

    @ApiModelProperty(value = "异常信息")
    private String errorMsg;

    @ApiModelProperty(value = "抛出异常类")
    private String throwExceptionClass;

    @ApiModelProperty(value = "创建日期")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "tenantId")
    private String tenantId;

}
