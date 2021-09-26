package com.github.sparkzxl.log.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * description: 操作日志实体类
 *
 * @author zhouxinlei
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "OptLogRecordDetail对象", description = "操作日志实体类")
public class OptLogRecordDetail implements Serializable {

    private static final long serialVersionUID = -7899035779553703264L;

    @ApiModelProperty(value = "请求IP")
    private String ip;

    @ApiModelProperty(value = "请求接口")
    private String requestUrl;

    @ApiModelProperty(value = "业务对象标识")
    private String bizNo;

    @ApiModelProperty(value = "操作日志的种类")
    private String category;

    @ApiModelProperty(value = "日志详情")
    private String detail;

    @ApiModelProperty(value = "操作人id")
    private String userId;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "租户")
    private String tenantId;

}
